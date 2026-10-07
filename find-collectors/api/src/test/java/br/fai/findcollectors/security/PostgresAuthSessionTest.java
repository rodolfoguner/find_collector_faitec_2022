package br.fai.findcollectors.security;

import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.enums.PersonType;
import br.fai.findcollectors.exceptions.InvalidTokenException;
import br.fai.findcollectors.repositories.AuthSessionRepository;
import br.fai.findcollectors.repositories.PersonRepository;
import br.fai.findcollectors.usecases.auth.AuthSessionService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "JWT_SECRET=test-secret-with-at-least-thirty-two-bytes")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@EnabledIfEnvironmentVariable(named = "RUN_POSTGRES_TESTS", matches = "true")
class PostgresAuthSessionTest {
    private static final String SCHEMA = "auth_session_test_" + UUID.randomUUID().toString().replace("-", "");

    @DynamicPropertySource
    static void isolateSchema(DynamicPropertyRegistry properties) {
        properties.add("spring.flyway.schemas", () -> SCHEMA);
        properties.add("spring.flyway.default-schema", () -> SCHEMA);
        properties.add("spring.jpa.properties.hibernate.default_schema", () -> SCHEMA);
        properties.add("spring.datasource.hikari.schema", () -> SCHEMA);
    }

    @AfterAll
    static void removeOnlyTestSchema(@Autowired JdbcTemplate jdbc) {
        jdbc.execute("DROP SCHEMA " + SCHEMA + " CASCADE");
    }

    @Autowired AuthSessionService service;
    @Autowired SessionTokenCodec codec;
    @MockitoSpyBean AuthSessionRepository sessions;
    @Autowired PersonRepository persons;
    @Autowired PasswordHasher passwords;
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    private Person person;

    @BeforeEach
    void createPerson() {
        person = persons.create(Person.builder().email("auth-test-" + UUID.randomUUID() + "@example.com")
                .name("Authentication test").personType(PersonType.COLLECTOR)
                .password(passwords.hash("TestPassword123")).build());
    }

    @AfterEach
    void removeOnlyTestData() {
        reset(sessions);
        persons.deleteById(person.getId());
    }

    @Test
    void logoutShouldRejectAllTokensOfSessionAndPreserveOtherLogins() throws Exception {
        IssuedTokens first = service.login(person);
        IssuedTokens rotated = service.refresh(first.refreshToken());
        IssuedTokens other = service.login(person);
        mvc.perform(get("/api/me").header("Authorization", "Bearer " + first.accessToken())).andExpect(status().isOk());
        mvc.perform(post("/api/logout").header("Authorization", "Bearer " + rotated.accessToken())).andExpect(status().isNoContent());
        for (String token : new String[]{first.accessToken(), rotated.accessToken()}) {
            mvc.perform(get("/api/me").header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value("INVALID_TOKEN"));
        }
        assertThatThrownBy(() -> service.refresh(rotated.refreshToken())).isInstanceOf(InvalidTokenException.class);
        mvc.perform(get("/api/me").header("Authorization", "Bearer " + other.accessToken())).andExpect(status().isOk());
        assertThat(service.refresh(other.refreshToken())).isNotNull();
    }

    @Test
    void replayRevocationShouldRemainCommittedAfterReturning401() throws Exception {
        IssuedTokens first = service.login(person);
        IssuedTokens rotated = service.refresh(first.refreshToken());
        mvc.perform(post("/api/refresh").contentType("application/json")
                        .content("{\"refreshToken\":\"" + first.refreshToken() + "\"}"))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value("INVALID_TOKEN"));
        UUID id = codec.readRefresh(rotated.refreshToken()).sessionId();
        assertThat(sessions.findById(id).orElseThrow().revokedAt()).isNotNull();
        assertThatThrownBy(() -> service.refresh(rotated.refreshToken())).isInstanceOf(InvalidTokenException.class);
        mvc.perform(get("/api/me").header("Authorization", "Bearer " + rotated.accessToken())).andExpect(status().isUnauthorized());
    }

    @Test
    void concurrentRefreshesShouldProduceAtMostOnePairAndRevokeOnReuse() throws Exception {
        IssuedTokens first = service.login(person);
        CountDownLatch start = new CountDownLatch(1);
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            Callable<Boolean> refresh = () -> {
                start.await();
                try {
                    service.refresh(first.refreshToken());
                    return true;
                } catch (InvalidTokenException expected) {
                    return false;
                }
            };
            Future<Boolean> a = executor.submit(refresh);
            Future<Boolean> b = executor.submit(refresh);
            start.countDown();
            assertThat(new Boolean[]{a.get(15, TimeUnit.SECONDS), b.get(15, TimeUnit.SECONDS)})
                    .containsExactlyInAnyOrder(true, false);
        }
        UUID id = codec.readRefresh(first.refreshToken()).sessionId();
        assertThat(sessions.isActive(id, person.getId(), Instant.now())).isFalse();
    }

    @Test
    void concurrentLogoutAndRefreshShouldLeaveNoUsableSession() throws Exception {
        IssuedTokens first = service.login(person);
        UUID id = codec.readRefresh(first.refreshToken()).sessionId();
        CountDownLatch start = new CountDownLatch(1);
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            Future<?> logout = executor.submit(() -> {
                try { start.await(); } catch (InterruptedException e) { throw new RuntimeException(e); }
                service.logout(id, person.getId());
            });
            Future<?> refresh = executor.submit(() -> {
                try {
                    start.await();
                    service.refresh(first.refreshToken());
                } catch (InvalidTokenException expected) {
                    // Logout won the race.
                } catch (InterruptedException e) { throw new RuntimeException(e); }
            });
            start.countDown();
            logout.get(15, TimeUnit.SECONDS);
            refresh.get(15, TimeUnit.SECONDS);
        }
        assertThat(sessions.isActive(id, person.getId(), Instant.now())).isFalse();
    }

    @Test
    void sessionStoreOutageShouldReturn503ForAccessAndRefresh() throws Exception {
        IssuedTokens first = service.login(person);
        doThrow(new br.fai.findcollectors.exceptions.AuthenticationUnavailableException(
                new RuntimeException("database details"))).when(sessions).isActive(any(), any(), any());
        mvc.perform(get("/api/me").header("Authorization", "Bearer " + first.accessToken()))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_UNAVAILABLE"));
        doThrow(new br.fai.findcollectors.exceptions.AuthenticationUnavailableException(
                new RuntimeException("database details"))).when(sessions).findById(any());
        mvc.perform(post("/api/refresh").contentType("application/json")
                        .content("{\"refreshToken\":\"" + first.refreshToken() + "\"}"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_UNAVAILABLE"));
    }

    @Test
    void openApiShouldDocumentLogoutAsProtectedWith204Response() throws Exception {
        mvc.perform(get("/v3/api-docs/find-collectors-api"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/logout'].post.responses['204']").exists())
                .andExpect(jsonPath("$.security[0].bearerAuth").exists());
    }

    @Test
    void cleanupShouldCascadeRefreshHistoryAndKeepActiveSessions() {
        IssuedTokens active = service.login(person);
        UUID expiredId = UUID.randomUUID();
        sessions.create(new AuthSession(expiredId, person.getId(), Instant.now().minusSeconds(200),
                Instant.now().minusSeconds(100), null), "a".repeat(64));
        sessions.deleteExpired(Instant.now());
        assertThat(sessions.findById(expiredId)).isEmpty();
        assertThat(jdbc.queryForObject("select count(*) from auth_refresh_tokens where session_id = ?",
                Long.class, expiredId)).isZero();
        assertThat(sessions.findById(codec.readRefresh(active.refreshToken()).sessionId())).isPresent();
    }
}

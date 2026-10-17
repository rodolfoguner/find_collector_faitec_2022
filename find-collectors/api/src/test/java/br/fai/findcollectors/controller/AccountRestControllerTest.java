package br.fai.findcollectors.controller;

import br.fai.findcollectors.config.GlobalExceptionHandler;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.exceptions.AuthenticationUnavailableException;
import br.fai.findcollectors.exceptions.ConflictException;
import br.fai.findcollectors.exceptions.ErrorCode;
import br.fai.findcollectors.exceptions.InvalidCredentialsException;
import br.fai.findcollectors.exceptions.InvalidTokenException;
import br.fai.findcollectors.security.IssuedTokens;
import br.fai.findcollectors.usecases.auth.AuthSessionService;
import br.fai.findcollectors.usecases.auth.AuthenticateUserUseCase;
import br.fai.findcollectors.usecases.person.CreatePersonUseCase;
import br.fai.findcollectors.usecases.person.PersonQueryUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AccountRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticateUserUseCase authenticateUserUseCase;
    @MockitoBean
    private CreatePersonUseCase createPersonUseCase;
    @MockitoBean
    private PersonQueryUseCase personQueryUseCase;
    @MockitoBean
    private AuthSessionService authSessionService;
    @MockitoBean(name = "jpaMappingContext")
    private JpaMetamodelMappingContext jpaMappingContext;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void loginAuthenticatesAndCreatesSession() throws Exception {
        Person person = ControllerTestFixtures.collector(7L);
        Instant accessExpiry = Instant.parse("2026-10-07T13:15:00Z");
        Instant refreshExpiry = Instant.parse("2026-10-14T13:00:00Z");
        when(authenticateUserUseCase.execute("collector@example.com", "StrongPass1")).thenReturn(person);
        when(authSessionService.login(person)).thenReturn(
                new IssuedTokens("access-token", accessExpiry, "refresh-token", refreshExpiry));

        mockMvc.perform(post("/api/login").contentType(MediaType.APPLICATION_JSON).content("""
                        {"email":"collector@example.com","password":"StrongPass1"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.accessTokenExpiresAt").value(accessExpiry.toString()))
                .andExpect(jsonPath("$.refreshTokenExpiresAt").value(refreshExpiry.toString()));

        verify(authenticateUserUseCase).execute("collector@example.com", "StrongPass1");
        verify(authSessionService).login(person);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}",
            "{\"email\":\"\",\"password\":\"StrongPass1\"}",
            "{\"email\":\"not-an-email\",\"password\":\"StrongPass1\"}",
            "{\"email\":\"collector@example.com\",\"password\":\"\"}"
    })
    void loginRejectsMissingBlankOrInvalidCredentials(String body) throws Exception {
        assertError(post("/api/login").contentType(MediaType.APPLICATION_JSON).content(body),
                400, "VALIDATION_ERROR", "/api/login");
        verify(authenticateUserUseCase, never()).execute(any(), any());
        verify(authSessionService, never()).login(any());
    }

    @Test
    void loginRejectsFieldsAboveLimits() throws Exception {
        String body = "{\"email\":\"" + "a".repeat(90) + "@example.com\",\"password\":\""
                + "x".repeat(101) + "\"}";
        assertError(post("/api/login").contentType(MediaType.APPLICATION_JSON).content(body),
                400, "VALIDATION_ERROR", "/api/login");
    }

    @Test
    void invalidCredentialsReturn401WithoutCreatingSession() throws Exception {
        when(authenticateUserUseCase.execute("collector@example.com", "wrong"))
                .thenThrow(new InvalidCredentialsException("Email or password is invalid"));

        assertError(post("/api/login").contentType(MediaType.APPLICATION_JSON).content("""
                        {"email":"collector@example.com","password":"wrong"}
                        """), 401, "INVALID_CREDENTIALS", "/api/login");
        verify(authSessionService, never()).login(any());
    }

    @Test
    void unexpectedLoginFailureDoesNotExposeInternalDetails() throws Exception {
        when(authenticateUserUseCase.execute(any(), any()))
                .thenThrow(new RuntimeException("jdbc password=database-secret"));

        mockMvc.perform(post("/api/login").contentType(MediaType.APPLICATION_JSON).content("""
                        {"email":"collector@example.com","password":"StrongPass1"}
                        """))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"))
                .andExpect(jsonPath("$.path").value("/api/login"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.exception").doesNotExist())
                .andExpect(content().string(not(containsString("database-secret"))));
    }

    @Test
    void refreshReturnsANewTokenPair() throws Exception {
        when(authSessionService.refresh("raw-refresh-token")).thenReturn(new IssuedTokens(
                "new-access", Instant.parse("2026-10-07T14:00:00Z"),
                "new-refresh", Instant.parse("2026-10-14T13:00:00Z")));

        mockMvc.perform(post("/api/refresh").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"raw-refresh-token\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").value("new-access"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"refreshToken\":\"\"}"})
    void refreshRejectsMissingOrBlankToken(String body) throws Exception {
        assertError(post("/api/refresh").contentType(MediaType.APPLICATION_JSON).content(body),
                400, "VALIDATION_ERROR", "/api/refresh");
        verify(authSessionService, never()).refresh(any());
    }

    @Test
    void refreshRejectsOversizedToken() throws Exception {
        String token = "sensitive-" + "x".repeat(4090);
        mockMvc.perform(post("/api/refresh").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + token + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/api/refresh"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.exception").doesNotExist())
                .andExpect(content().string(not(containsString(token))));
    }

    @Test
    void invalidExpiredOrReusedRefreshTokenReturns401WithoutEchoingToken() throws Exception {
        String token = "raw-sensitive-refresh-token";
        when(authSessionService.refresh(token)).thenThrow(new InvalidTokenException("Refresh token is invalid"));

        mockMvc.perform(post("/api/refresh").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + token + "\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_TOKEN"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/api/refresh"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.exception").doesNotExist())
                .andExpect(content().string(not(containsString(token))));
    }

    @Test
    void refreshReturns503WhenSessionStorageIsUnavailable() throws Exception {
        when(authSessionService.refresh("raw-refresh-token"))
                .thenThrow(new AuthenticationUnavailableException(new RuntimeException("database-secret")));

        mockMvc.perform(post("/api/refresh").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"raw-refresh-token\"}"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_UNAVAILABLE"))
                .andExpect(jsonPath("$.message").value("Authentication is temporarily unavailable"))
                .andExpect(jsonPath("$.path").value("/api/refresh"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.exception").doesNotExist())
                .andExpect(content().string(not(containsString("database-secret"))));
    }

    @Test
    void logoutPassesTypedClaimsAndReturnsNoContent() throws Exception {
        UUID sessionId = UUID.randomUUID();
        authenticateJwt("collector@example.com", sessionId, 42L);

        mockMvc.perform(post("/api/logout"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(authSessionService).logout(sessionId, 42L);
    }

    @Test
    void signupMapsRequestAndNeverReturnsPassword() throws Exception {
        when(createPersonUseCase.execute(any())).thenReturn(ControllerTestFixtures.recycler(11L));

        mockMvc.perform(post("/api/signup").contentType(MediaType.APPLICATION_JSON).content("""
                        {"email":"recycler@example.com","password":"StrongPass1",
                         "name":"Recycler","personType":"RECYCLER"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.email").value("recycler@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(content().string(not(containsString("StrongPass1"))));

        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        verify(createPersonUseCase).execute(captor.capture());
        assertThat(captor.getValue())
                .extracting(Person::getEmail, Person::getPassword, Person::getName, Person::getPersonType)
                .containsExactly("recycler@example.com", "StrongPass1", "Recycler",
                        br.fai.findcollectors.enums.PersonType.RECYCLER);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"email\":\"invalid\",\"password\":\"StrongPass1\",\"name\":\"Name\",\"personType\":\"RECYCLER\"}",
            "{\"email\":\"a@b.com\",\"password\":\"weak\",\"name\":\"Name\",\"personType\":\"RECYCLER\"}",
            "{\"email\":\"a@b.com\",\"password\":\"StrongPass1\",\"name\":\"\",\"personType\":\"RECYCLER\"}",
            "{\"email\":\"a@b.com\",\"password\":\"StrongPass1\",\"name\":\"Name\",\"personType\":\"ADMIN\"}"
    })
    void signupRejectsInvalidPayloadWithoutCallingUseCase(String body) throws Exception {
        assertError(post("/api/signup").contentType(MediaType.APPLICATION_JSON).content(body),
                400, "VALIDATION_ERROR", "/api/signup");
        verify(createPersonUseCase, never()).execute(any());
    }

    @Test
    void signupReturnsConflictForRegisteredEmail() throws Exception {
        when(createPersonUseCase.execute(any())).thenThrow(new ConflictException(
                ErrorCode.EMAIL_ALREADY_REGISTERED, "Email already registered"));

        assertError(post("/api/signup").contentType(MediaType.APPLICATION_JSON).content("""
                        {"email":"recycler@example.com","password":"StrongPass1",
                         "name":"Recycler","personType":"RECYCLER"}
                        """), 409, "EMAIL_ALREADY_REGISTERED", "/api/signup");
    }

    @Test
    void meUsesJwtSubjectAndDoesNotExposeSensitiveFields() throws Exception {
        when(personQueryUseCase.findPersonByEmail("collector@example.com"))
                .thenReturn(ControllerTestFixtures.collector(42L));
        authenticateJwt("collector@example.com", UUID.randomUUID(), 42L);

        mockMvc.perform(get("/api/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("collector@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.authentication").doesNotExist());

        verify(personQueryUseCase).findPersonByEmail("collector@example.com");
    }

    @Test
    void meReturnsInvalidTokenWhenOwnerDoesNotExist() throws Exception {
        when(personQueryUseCase.findPersonByEmail("gone@example.com")).thenReturn(null);
        authenticateJwt("gone@example.com", UUID.randomUUID(), 42L);

        assertError(get("/api/me"),
                401, "INVALID_TOKEN", "/api/me");
    }

    private void authenticateJwt(String subject, UUID sessionId, long personId) {
        Jwt token = Jwt.withTokenValue("contract-test-token")
                .header("alg", "none")
                .subject(subject)
                .claim("sid", sessionId.toString())
                .claim("personId", personId)
                .build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(token));
    }

    private void assertError(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder request,
            int expectedStatus,
            String code,
            String path
    ) throws Exception {
        mockMvc.perform(request)
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value(path))
                .andExpect(jsonPath("$.timestamp", matchesPattern(
                        "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?Z$")))
                .andExpect(jsonPath("$.exception").doesNotExist())
                .andExpect(jsonPath("$.stackTrace").doesNotExist());
    }
}

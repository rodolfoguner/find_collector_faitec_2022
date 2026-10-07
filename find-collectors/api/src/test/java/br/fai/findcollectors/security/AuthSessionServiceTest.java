package br.fai.findcollectors.security;

import br.fai.findcollectors.config.JwtProperties;
import br.fai.findcollectors.config.SecurityConfig;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.enums.PersonType;
import br.fai.findcollectors.exceptions.InvalidTokenException;
import br.fai.findcollectors.repositories.AuthSessionRepository;
import br.fai.findcollectors.repositories.PersonRepository;
import br.fai.findcollectors.usecases.auth.AuthSessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthSessionServiceTest {
    private final AuthSessionRepository sessions = mock(AuthSessionRepository.class);
    private final PersonRepository persons = mock(PersonRepository.class);
    private AuthSessionService service;
    private JwtTokenService codec;
    private final Person person = Person.builder().id(10L).email("collector@example.com")
            .personType(PersonType.COLLECTOR).build();

    @BeforeEach
    void setUp() {
        var config = new SecurityConfig();
        var properties = new JwtProperties("test-secret-with-at-least-thirty-two-bytes", "test", Duration.ofMinutes(15), Duration.ofDays(7));
        codec = new JwtTokenService(config.jwtEncoder(properties), config.refreshTokenDecoder(properties), properties);
        service = new AuthSessionService(sessions, persons, codec, Clock.systemUTC(), Duration.ofMinutes(15), Duration.ofDays(7));
        when(persons.findById(10L)).thenReturn(Optional.of(person));
    }

    @Test
    void shouldCreateIndependentSessionsAndStoreOnlyRefreshHashes() {
        IssuedTokens first = service.login(person);
        IssuedTokens second = service.login(person);
        UUID firstId = codec.readRefresh(first.refreshToken()).sessionId();
        assertThat(codec.readRefresh(second.refreshToken()).sessionId()).isNotEqualTo(firstId);
        var hash = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(sessions, times(2)).create(any(), hash.capture());
        assertThat(hash.getAllValues()).allMatch(value -> value.matches("[0-9a-f]{64}"));
        assertThat(hash.getAllValues()).doesNotContain(first.refreshToken(), second.refreshToken());
    }

    @Test
    void shouldKeepSessionAndAbsoluteExpirationOnRefresh() {
        IssuedTokens first = service.login(person);
        UUID id = codec.readRefresh(first.refreshToken()).sessionId();
        Instant deadline = Instant.now().plusSeconds(120).truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
        when(sessions.findById(id)).thenReturn(Optional.of(new AuthSession(id, 10L,
                Instant.now().minusSeconds(100), deadline, null)));
        when(sessions.rotate(eq(id), eq(10L), anyString(), anyString(), any()))
                .thenReturn(AuthSessionRepository.RotationResult.ROTATED);
        IssuedTokens next = service.refresh(first.refreshToken());
        assertThat(codec.readRefresh(next.refreshToken()).sessionId()).isEqualTo(id);
        assertThat(next.refreshToken()).isNotEqualTo(first.refreshToken());
        assertThat(next.refreshTokenExpiresAt()).isEqualTo(deadline);
        assertThat(next.accessTokenExpiresAt()).isEqualTo(deadline);
    }

    @Test
    void shouldRejectReusedRefreshAfterRepositoryCommitsRevocation() {
        IssuedTokens first = service.login(person);
        UUID id = codec.readRefresh(first.refreshToken()).sessionId();
        when(sessions.findById(id)).thenReturn(Optional.of(new AuthSession(id, 10L, Instant.now(), first.refreshTokenExpiresAt(), null)));
        when(sessions.rotate(eq(id), eq(10L), anyString(), anyString(), any()))
                .thenReturn(AuthSessionRepository.RotationResult.REUSED);
        assertThatThrownBy(() -> service.refresh(first.refreshToken())).isInstanceOf(InvalidTokenException.class);
        verify(sessions).rotate(eq(id), eq(10L), anyString(), anyString(), any());
    }

    @Test
    void shouldRejectExpiredRevokedUnknownAndWrongOwnerSessions() {
        IssuedTokens first = service.login(person);
        UUID id = codec.readRefresh(first.refreshToken()).sessionId();
        for (AuthSession session : List.of(
                new AuthSession(id, 10L, Instant.now().minusSeconds(100), Instant.now().minusSeconds(1), null),
                new AuthSession(id, 10L, Instant.now(), first.refreshTokenExpiresAt(), Instant.now()),
                new AuthSession(id, 99L, Instant.now(), first.refreshTokenExpiresAt(), null))) {
            when(sessions.findById(id)).thenReturn(Optional.of(session));
            assertThatThrownBy(() -> service.refresh(first.refreshToken())).isInstanceOf(InvalidTokenException.class);
        }
        when(sessions.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.refresh(first.refreshToken())).isInstanceOf(InvalidTokenException.class);
        verify(sessions, never()).rotate(any(), any(), anyString(), anyString(), any());
    }

    @Test
    void shouldRevokeOnlyTheCurrentSessionOnLogout() {
        UUID id = UUID.randomUUID();
        service.logout(id, 10L);
        verify(sessions).revoke(eq(id), eq(10L), any());
    }
}

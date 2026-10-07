package br.fai.findcollectors.usecases.auth;

import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.enums.PersonType;
import br.fai.findcollectors.exceptions.ErrorCode;
import br.fai.findcollectors.exceptions.InvalidTokenException;
import br.fai.findcollectors.repositories.AuthSessionRepository;
import br.fai.findcollectors.repositories.PersonRepository;
import br.fai.findcollectors.security.AuthSession;
import br.fai.findcollectors.security.IssuedTokens;
import br.fai.findcollectors.security.RefreshTokenIdentity;
import br.fai.findcollectors.security.SessionTokenCodec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AuthSessionServiceTest {
    private static final Instant NOW = Instant.parse("2026-10-07T15:30:45.987Z");
    private static final Instant TRUNCATED_NOW = Instant.parse("2026-10-07T15:30:45Z");
    private static final Duration ACCESS_TTL = Duration.ofMinutes(15);
    private static final Duration SESSION_TTL = Duration.ofDays(7);
    private static final String INVALID_TOKEN_MESSAGE = "Token is invalid, expired or revoked";
    private static final String OLD_REFRESH_HASH = "93dd6d39cf0a73f62182459fbcbbd0dfaa9974b15193ad5b609fe181f7b0c6fa";
    private static final String NEW_REFRESH_HASH = "149d5e49de35bb02c7539da4776649f8fc61ca6a62270ef3f415d93ba216ef43";

    private final AuthSessionRepository sessions = mock(AuthSessionRepository.class);
    private final PersonRepository persons = mock(PersonRepository.class);
    private final SessionTokenCodec tokens = mock(SessionTokenCodec.class);
    private final Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
    private final Person person = Person.builder().id(10L).email("collector@example.com")
            .personType(PersonType.COLLECTOR).build();
    private final AuthSessionService service = service(ACCESS_TTL, SESSION_TTL);

    @ParameterizedTest
    @MethodSource("invalidLifetimes")
    void shouldRejectNonPositiveTokenLifetimes(Duration accessTtl, Duration sessionTtl) {
        assertThatThrownBy(() -> service(accessTtl, sessionTtl))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Token lifetimes must be positive");
    }

    static Stream<Arguments> invalidLifetimes() {
        return Stream.of(
                Arguments.of(Duration.ZERO, SESSION_TTL),
                Arguments.of(Duration.ofSeconds(-1), SESSION_TTL),
                Arguments.of(ACCESS_TTL, Duration.ZERO),
                Arguments.of(ACCESS_TTL, Duration.ofSeconds(-1))
        );
    }

    @Test
    void shouldCreateSessionStoreOnlyRefreshHashAndReturnIssuedTokensOnLogin() {
        Instant deadline = TRUNCATED_NOW.plus(SESSION_TTL);
        IssuedTokens issued = new IssuedTokens("access", TRUNCATED_NOW.plus(ACCESS_TTL),
                "refresh-token", deadline);
        when(tokens.issue(eq(person), any(UUID.class), eq(TRUNCATED_NOW),
                eq(TRUNCATED_NOW.plus(ACCESS_TTL)), eq(deadline))).thenReturn(issued);

        IssuedTokens result = service.login(person);

        assertThat(result).isSameAs(issued);
        ArgumentCaptor<AuthSession> sessionCaptor = ArgumentCaptor.forClass(AuthSession.class);
        verify(sessions).create(sessionCaptor.capture(),
                eq("0eb17643d4e9261163783a420859c92c7d212fa9624106a12b510afbec266120"));
        AuthSession created = sessionCaptor.getValue();
        assertThat(created.id()).isNotNull();
        assertThat(created.personId()).isEqualTo(10L);
        assertThat(created.createdAt()).isEqualTo(TRUNCATED_NOW);
        assertThat(created.expiresAt()).isEqualTo(deadline);
        assertThat(created.revokedAt()).isNull();
        verify(tokens).issue(person, created.id(), TRUNCATED_NOW,
                TRUNCATED_NOW.plus(ACCESS_TTL), deadline);
    }

    @Test
    void shouldCapAccessTokenExpirationAtAbsoluteSessionExpiration() {
        AuthSessionService cappedService = service(Duration.ofHours(2), Duration.ofMinutes(30));
        Instant deadline = TRUNCATED_NOW.plus(Duration.ofMinutes(30));
        IssuedTokens issued = new IssuedTokens("access", deadline, "refresh", deadline);
        when(tokens.issue(eq(person), any(UUID.class), eq(TRUNCATED_NOW), eq(deadline), eq(deadline)))
                .thenReturn(issued);

        assertThat(cappedService.login(person)).isSameAs(issued);
        verify(tokens).issue(eq(person), any(UUID.class), eq(TRUNCATED_NOW), eq(deadline), eq(deadline));
    }

    @Test
    void shouldRotateValidRefreshKeepingSessionIdAndUsingNewTimeLimitsAndHashes() {
        UUID sessionId = UUID.fromString("25d9edb5-2850-4856-b56d-2d3452d001d2");
        Instant deadline = TRUNCATED_NOW.plus(Duration.ofHours(1));
        AuthSession session = new AuthSession(sessionId, person.getId(),
                TRUNCATED_NOW.minus(Duration.ofDays(1)), deadline, null);
        RefreshTokenIdentity identity = new RefreshTokenIdentity(sessionId, person.getId(), person.getEmail());
        IssuedTokens issued = new IssuedTokens("new-access", TRUNCATED_NOW.plus(ACCESS_TTL),
                "new-refresh", deadline);
        when(tokens.readRefresh("old-refresh")).thenReturn(identity);
        when(sessions.findById(sessionId)).thenReturn(Optional.of(session));
        when(persons.findById(person.getId())).thenReturn(Optional.of(person));
        when(tokens.issue(person, sessionId, TRUNCATED_NOW, TRUNCATED_NOW.plus(ACCESS_TTL), deadline))
                .thenReturn(issued);
        when(sessions.rotate(sessionId, person.getId(), OLD_REFRESH_HASH, NEW_REFRESH_HASH, TRUNCATED_NOW))
                .thenReturn(AuthSessionRepository.RotationResult.ROTATED);

        assertThat(service.refresh("old-refresh")).isSameAs(issued);
        verify(tokens).issue(person, sessionId, TRUNCATED_NOW, TRUNCATED_NOW.plus(ACCESS_TTL), deadline);
        verify(sessions).rotate(sessionId, person.getId(), OLD_REFRESH_HASH, NEW_REFRESH_HASH, TRUNCATED_NOW);
    }

    @Test
    void shouldRejectRefreshWhenSessionDoesNotExist() {
        UUID sessionId = UUID.randomUUID();
        when(tokens.readRefresh("old-refresh"))
                .thenReturn(new RefreshTokenIdentity(sessionId, person.getId(), person.getEmail()));
        when(sessions.findById(sessionId)).thenReturn(Optional.empty());

        assertInvalidToken(() -> service.refresh("old-refresh"));
        verifyNoInteractions(persons);
        verify(tokens, never()).issue(any(), any(), any(), any(), any());
        verify(sessions, never()).rotate(any(), any(), any(), any(), any());
    }

    @ParameterizedTest
    @MethodSource("inactiveOrWrongOwnerSessions")
    void shouldRejectExpiredRevokedOrWrongOwnerSession(AuthSession session) {
        when(tokens.readRefresh("old-refresh"))
                .thenReturn(new RefreshTokenIdentity(session.id(), person.getId(), person.getEmail()));
        when(sessions.findById(session.id())).thenReturn(Optional.of(session));

        assertInvalidToken(() -> service.refresh("old-refresh"));
        verifyNoInteractions(persons);
        verify(tokens, never()).issue(any(), any(), any(), any(), any());
        verify(sessions, never()).rotate(any(), any(), any(), any(), any());
    }

    static Stream<AuthSession> inactiveOrWrongOwnerSessions() {
        UUID id = UUID.fromString("8ece89d7-ff57-43b2-a0e1-c308c94d2030");
        return Stream.of(
                new AuthSession(id, 10L, TRUNCATED_NOW.minusSeconds(60), TRUNCATED_NOW, null),
                new AuthSession(id, 10L, TRUNCATED_NOW.minusSeconds(60), TRUNCATED_NOW.plusSeconds(60), TRUNCATED_NOW),
                new AuthSession(id, 99L, TRUNCATED_NOW.minusSeconds(60), TRUNCATED_NOW.plusSeconds(60), null)
        );
    }

    @Test
    void shouldRejectRefreshWhenPersonDoesNotExist() {
        UUID sessionId = stubActiveSession();
        when(persons.findById(person.getId())).thenReturn(Optional.empty());

        assertInvalidToken(() -> service.refresh("old-refresh"));
        verify(tokens, never()).issue(any(), any(), any(), any(), any());
        verify(sessions, never()).rotate(any(), any(), any(), any(), any());
        verify(sessions).findById(sessionId);
    }

    @Test
    void shouldRejectRefreshWhenSubjectDoesNotMatchPersonEmail() {
        UUID sessionId = UUID.randomUUID();
        AuthSession session = activeSession(sessionId);
        when(tokens.readRefresh("old-refresh"))
                .thenReturn(new RefreshTokenIdentity(sessionId, person.getId(), "other@example.com"));
        when(sessions.findById(sessionId)).thenReturn(Optional.of(session));
        when(persons.findById(person.getId())).thenReturn(Optional.of(person));

        assertInvalidToken(() -> service.refresh("old-refresh"));
        verify(tokens, never()).issue(any(), any(), any(), any(), any());
        verify(sessions, never()).rotate(any(), any(), any(), any(), any());
    }

    @ParameterizedTest
    @EnumSource(value = AuthSessionRepository.RotationResult.class, names = {"REUSED", "INVALID"})
    void shouldRejectUnsuccessfulRefreshRotation(AuthSessionRepository.RotationResult result) {
        UUID sessionId = stubRefreshReadyToRotate();
        when(sessions.rotate(sessionId, person.getId(), OLD_REFRESH_HASH, NEW_REFRESH_HASH, TRUNCATED_NOW))
                .thenReturn(result);

        assertInvalidToken(() -> service.refresh("old-refresh"));
        verify(sessions).rotate(sessionId, person.getId(), OLD_REFRESH_HASH, NEW_REFRESH_HASH, TRUNCATED_NOW);
    }

    @Test
    void shouldNotConsumeCurrentRefreshWhenNewTokenIssuanceFails() {
        UUID sessionId = stubActiveSession();
        when(persons.findById(person.getId())).thenReturn(Optional.of(person));
        when(tokens.issue(eq(person), eq(sessionId), any(), any(), any()))
                .thenThrow(new IllegalStateException("signing failed"));

        assertThatThrownBy(() -> service.refresh("old-refresh"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("signing failed");
        verify(sessions, never()).rotate(any(), any(), any(), any(), any());
    }

    @Test
    void shouldRevokeExactSessionForPersonAtClockInstantOnLogout() {
        UUID sessionId = UUID.randomUUID();
        service.logout(sessionId, person.getId());
        verify(sessions).revoke(sessionId, person.getId(), NOW);
    }

    private AuthSessionService service(Duration accessTtl, Duration sessionTtl) {
        return new AuthSessionService(sessions, persons, tokens, clock, accessTtl, sessionTtl);
    }

    private UUID stubActiveSession() {
        UUID sessionId = UUID.randomUUID();
        when(tokens.readRefresh("old-refresh"))
                .thenReturn(new RefreshTokenIdentity(sessionId, person.getId(), person.getEmail()));
        when(sessions.findById(sessionId)).thenReturn(Optional.of(activeSession(sessionId)));
        return sessionId;
    }

    private UUID stubRefreshReadyToRotate() {
        UUID sessionId = stubActiveSession();
        AuthSession session = activeSession(sessionId);
        when(sessions.findById(sessionId)).thenReturn(Optional.of(session));
        when(persons.findById(person.getId())).thenReturn(Optional.of(person));
        when(tokens.issue(person, sessionId, TRUNCATED_NOW, TRUNCATED_NOW.plus(ACCESS_TTL), session.expiresAt()))
                .thenReturn(new IssuedTokens("new-access", TRUNCATED_NOW.plus(ACCESS_TTL),
                        "new-refresh", session.expiresAt()));
        return sessionId;
    }

    private static AuthSession activeSession(UUID sessionId) {
        return new AuthSession(sessionId, 10L, TRUNCATED_NOW.minus(Duration.ofDays(1)),
                TRUNCATED_NOW.plus(Duration.ofHours(1)), null);
    }

    private static void assertInvalidToken(Runnable action) {
        assertThatThrownBy(action::run)
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage(INVALID_TOKEN_MESSAGE)
                .extracting("code").isEqualTo(ErrorCode.INVALID_TOKEN);
    }
}

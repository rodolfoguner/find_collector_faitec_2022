package br.fai.findcollectors.usecases.auth;

import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.exceptions.InvalidTokenException;
import br.fai.findcollectors.repositories.AuthSessionRepository;
import br.fai.findcollectors.repositories.PersonRepository;
import br.fai.findcollectors.security.AuthSession;
import br.fai.findcollectors.security.IssuedTokens;
import br.fai.findcollectors.security.RefreshTokenIdentity;
import br.fai.findcollectors.security.SessionTokenCodec;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.UUID;

public class AuthSessionService {
    private final AuthSessionRepository sessions;
    private final PersonRepository persons;
    private final SessionTokenCodec tokens;
    private final Clock clock;
    private final Duration accessTtl;
    private final Duration sessionTtl;

    public AuthSessionService(AuthSessionRepository sessions, PersonRepository persons,
                              SessionTokenCodec tokens, Clock clock, Duration accessTtl, Duration sessionTtl) {
        if (accessTtl.isNegative() || accessTtl.isZero() || sessionTtl.isNegative() || sessionTtl.isZero()) {
            throw new IllegalArgumentException("Token lifetimes must be positive");
        }
        this.sessions = sessions;
        this.persons = persons;
        this.tokens = tokens;
        this.clock = clock;
        this.accessTtl = accessTtl;
        this.sessionTtl = sessionTtl;
    }

    public IssuedTokens login(Person person) {
        Instant now = clock.instant().truncatedTo(ChronoUnit.SECONDS);
        AuthSession session = new AuthSession(UUID.randomUUID(), person.getId(), now, now.plus(sessionTtl), null);
        IssuedTokens issued = issue(person, session, now);
        sessions.create(session, hash(issued.refreshToken()));
        return issued;
    }

    public IssuedTokens refresh(String refreshToken) {
        RefreshTokenIdentity identity = tokens.readRefresh(refreshToken);
        Instant now = clock.instant().truncatedTo(ChronoUnit.SECONDS);
        AuthSession session = sessions.findById(identity.sessionId()).orElseThrow(this::invalidToken);
        if (!session.isActive(now) || !session.personId().equals(identity.personId())) {
            throw invalidToken();
        }
        Person person = persons.findById(session.personId()).orElseThrow(this::invalidToken);
        if (!person.getEmail().equals(identity.subject())) {
            throw invalidToken();
        }
        // Encode before consuming: signing failures must not consume the current refresh.
        IssuedTokens issued = issue(person, session, now);
        var result = sessions.rotate(session.id(), person.getId(), hash(refreshToken),
                hash(issued.refreshToken()), now);
        if (result != AuthSessionRepository.RotationResult.ROTATED) {
            // Deliberately outside the persistence transaction so replay revocation is committed.
            throw invalidToken();
        }
        return issued;
    }

    public void logout(UUID sessionId, Long personId) {
        sessions.revoke(sessionId, personId, clock.instant());
    }

    private IssuedTokens issue(Person person, AuthSession session, Instant now) {
        Instant accessExpires = now.plus(accessTtl);
        if (accessExpires.isAfter(session.expiresAt())) {
            accessExpires = session.expiresAt();
        }
        return tokens.issue(person, session.id(), now, accessExpires, session.expiresAt());
    }

    private InvalidTokenException invalidToken() {
        return new InvalidTokenException("Token is invalid, expired or revoked");
    }

    private static String hash(String token) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}

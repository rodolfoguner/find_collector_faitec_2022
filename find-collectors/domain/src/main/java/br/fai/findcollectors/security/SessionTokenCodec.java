package br.fai.findcollectors.security;

import br.fai.findcollectors.entities.Person;
import java.time.Instant;
import java.util.UUID;

public interface SessionTokenCodec {
    IssuedTokens issue(Person person, UUID sessionId, Instant issuedAt,
                       Instant accessExpiresAt, Instant sessionExpiresAt);
    RefreshTokenIdentity readRefresh(String token);
}

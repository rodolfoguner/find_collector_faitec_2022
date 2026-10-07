package br.fai.findcollectors.repositories;

import br.fai.findcollectors.security.AuthSession;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface AuthSessionRepository {
    enum RotationResult { ROTATED, REUSED, INVALID }

    void create(AuthSession session, String refreshHash);
    Optional<AuthSession> findById(UUID id);
    boolean isActive(UUID id, Long personId, Instant now);

    // Atomic with logout; REUSED must commit the session revocation before returning.
    RotationResult rotate(UUID id, Long personId, String currentHash, String nextHash, Instant now);
    void revoke(UUID id, Long personId, Instant now);
    int deleteExpired(Instant now);
}

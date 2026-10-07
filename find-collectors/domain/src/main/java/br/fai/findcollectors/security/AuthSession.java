package br.fai.findcollectors.security;

import br.fai.findcollectors.repositories.AuthSessionRepository.RotationResult;
import java.time.Instant;
import java.util.UUID;

public record AuthSession(UUID id, Long personId, Instant createdAt, Instant expiresAt, Instant revokedAt) {
    public boolean isActive(Instant now) {
        return revokedAt == null && expiresAt.isAfter(now);
    }

    public RotationResult decideRotation(Long ownerId, Instant now, boolean knownRefresh, Instant consumedAt) {
        if (!personId.equals(ownerId) || !isActive(now) || !knownRefresh) {
            return RotationResult.INVALID;
        }
        return consumedAt == null ? RotationResult.ROTATED : RotationResult.REUSED;
    }
}

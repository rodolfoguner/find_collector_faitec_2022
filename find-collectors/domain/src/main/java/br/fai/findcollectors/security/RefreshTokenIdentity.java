package br.fai.findcollectors.security;

import java.util.UUID;

public record RefreshTokenIdentity(UUID sessionId, Long personId, String subject) {
}

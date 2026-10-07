package br.fai.findcollectors.dto.response;

import java.time.Instant;

public record AuthResponse(

    String tokenType,
    String accessToken,
    Instant accessTokenExpiresAt,
    String refreshToken,
    Instant refreshTokenExpiresAt
) {}

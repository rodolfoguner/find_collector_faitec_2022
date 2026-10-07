package br.fai.findcollectors.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RefreshTokenRequest(

    @NotBlank(message = "refreshToken is required")
    @Size(max = 4096, message = "refreshToken is too long")
    String refreshToken
) {}

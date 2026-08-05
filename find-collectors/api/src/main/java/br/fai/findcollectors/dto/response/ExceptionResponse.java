package br.fai.findcollectors.dto.response;

import java.time.Instant;

public record ExceptionResponse(
    String code,
    String message,
    String path,
    Instant timestamp
) {}

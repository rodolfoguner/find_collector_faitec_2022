package br.fai.findcollectors.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.List;

public record CreateCollectRequest(

    @NotNull(message = "dateAndTime is required")
    LocalDateTime dateAndTime,

    @NotEmpty(message = "garbageType is required")
    List<@NotNull(message = "garbageType must not contain null values") String> garbageType,

    boolean recurrent,

    @NotNull(message = "address is required")
    @Valid
    AddressRequest address,

    @NotNull(message = "recyclerId is required")
    @Positive(message = "recyclerId must be positive")
    Long recyclerId
) {}

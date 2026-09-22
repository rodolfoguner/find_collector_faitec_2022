package br.fai.findcollectors.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record UpdateCollectRequest(

    @NotNull(message = "dateAndTime is required")
    LocalDateTime dateAndTime,

    @NotEmpty(message = "garbageType is required")
    List<@NotNull(message = "garbageType must not contain null values") String> garbageType,

    boolean recurrent,

    @NotNull(message = "address is required")
    @Valid
    AddressRequest address
) {}

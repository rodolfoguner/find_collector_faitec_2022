package br.fai.findcollectors.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;
import java.util.List;

public record UpdateCollectRequest(

    @NotNull(message = "dateAndTime is required")
    LocalDateTime dateAndTime,

    @NotEmpty(message = "garbageType is required")
    List<
        @NotNull(message = "garbageType must not contain null values")
        @NotBlank(message = "garbageType must not contain blank values")
        @Pattern(
            regexp = "(?i)PAPER|GLASS|PLASTIC|METAL|ORGANIC|NO_RECICLABLE",
            message = "garbageType contains an invalid value"
        ) String
    > garbageType,

    boolean recurrent,

    @NotNull(message = "address is required")
    @Valid
    AddressRequest address
) {}

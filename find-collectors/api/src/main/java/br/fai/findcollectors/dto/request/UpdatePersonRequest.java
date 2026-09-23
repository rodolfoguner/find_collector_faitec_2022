package br.fai.findcollectors.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdatePersonRequest(

    @NotBlank(message = "name is required")
    @Size(max = 100, message = "name must have at most 100 characters")
    String name,

    @Size(max = 15, message = "telephone must have at most 15 characters")
    String telephone,

    @NotBlank(message = "personType is required")
    @Pattern(
        regexp = "(?i)RECYCLER|COLLECTOR",
        message = "personType must be RECYCLER or COLLECTOR"
    )
    String personType,

    boolean collectPoint,

    @NotNull(message = "garbageType is required")
    List<
        @NotBlank(message = "garbageType must not contain blank values")
        @Pattern(
            regexp = "(?i)PAPER|GLASS|PLASTIC|METAL|ORGANIC|NO_RECICLABLE",
            message = "garbageType contains an invalid value"
        ) String
    > garbageType,

    String description,

    @Valid
    AddressRequest address
) {}

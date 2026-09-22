package br.fai.findcollectors.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(

    @NotBlank(message = "cep is required")
    String cep,

    @NotBlank(message = "street is required")
    String street,

    @NotBlank(message = "district is required")
    String district,

    String number,

    @NotBlank(message = "city is required")
    String city,

    @NotBlank(message = "state is required")
    String state
) {}

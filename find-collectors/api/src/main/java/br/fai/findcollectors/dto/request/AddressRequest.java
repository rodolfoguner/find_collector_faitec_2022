package br.fai.findcollectors.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressRequest(

    @NotBlank(message = "cep is required")
    @Size(max = 10, message = "cep must have at most 10 characters")
    String cep,

    @NotBlank(message = "street is required")
    @Size(max = 100, message = "street must have at most 100 characters")
    String street,

    @NotBlank(message = "district is required")
    @Size(max = 100, message = "district must have at most 100 characters")
    String district,

    @Size(max = 10, message = "number must have at most 10 characters")
    String number,

    @NotBlank(message = "city is required")
    @Size(max = 100, message = "city must have at most 100 characters")
    String city,

    @NotBlank(message = "state is required")
    @Size(max = 50, message = "state must have at most 50 characters")
    String state
) {}

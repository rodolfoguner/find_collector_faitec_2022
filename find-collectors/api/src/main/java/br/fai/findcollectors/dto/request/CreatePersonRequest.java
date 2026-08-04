package br.fai.findcollectors.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePersonRequest(

    @NotNull(message = "email is required")
    @Email(message = "email needs to be valid")
    String email,
    
    @NotBlank(message = "password is required")
    String password,
    
    @NotBlank(message = "name is required")
    String name,
    
    @NotBlank(message = "personType is required")
    String personType
) {}

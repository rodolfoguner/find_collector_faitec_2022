package br.fai.findcollectors.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthRequest(
    
    @NotNull(message = "email is required")
    @Email(message = "email needs to be valid")
    String email,
    
    @NotBlank(message = "password is required")
    String password
) {}

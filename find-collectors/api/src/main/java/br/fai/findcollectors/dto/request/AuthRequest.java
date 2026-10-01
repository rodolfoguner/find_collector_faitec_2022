package br.fai.findcollectors.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(
    
    @NotBlank(message = "email is required")
    @Email(message = "email needs to be valid")
    @Size(max = 100, message = "email must have at most 100 characters")
    String email,
    
    @NotBlank(message = "password is required")
    @Size(max = 100, message = "password must have at most 100 characters")
    String password
) {}

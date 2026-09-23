package br.fai.findcollectors.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreatePersonRequest(

    @NotBlank(message = "email is required")
    @Email(message = "email needs to be valid")
    @Size(max = 100, message = "email must have at most 100 characters")
    String email,
    
    @NotBlank(message = "password is required")
    @Size(max = 100, message = "password must have at most 100 characters")
    String password,
    
    @NotBlank(message = "name is required")
    @Size(max = 100, message = "name must have at most 100 characters")
    String name,
    
    @NotBlank(message = "personType is required")
    @Pattern(
        regexp = "(?i)RECYCLER|COLLECTOR",
        message = "personType must be RECYCLER or COLLECTOR"
    )
    String personType
) {}

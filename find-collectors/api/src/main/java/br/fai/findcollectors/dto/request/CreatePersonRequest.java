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
    @Size(
        min = 8,
        max = 100,
        message = "password must have between 8 and 100 characters"
    )
    @Pattern(
        regexp = ".*[0-9].*",
        message = "password must contain at least one number"
    )
    @Pattern(
        regexp = ".*[A-Z].*",
        message = "password must contain at least one uppercase letter"
    )
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

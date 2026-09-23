package br.fai.findcollectors.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PersonRequestValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @Test
    void shouldAcceptValidCreatePersonRequest() {
        CreatePersonRequest request = new CreatePersonRequest(
                "recycler@example.com",
                "password",
                "Recycler",
                "recycler"
        );

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void shouldRejectInvalidCreatePersonFields() {
        CreatePersonRequest request = new CreatePersonRequest(" ", " ", " ", "ADMIN");

        Set<ConstraintViolation<CreatePersonRequest>> violations = validator.validate(request);

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("email", "password", "name", "personType");
    }

    @Test
    void shouldAcceptValidUpdatePersonRequest() {
        UpdatePersonRequest request = new UpdatePersonRequest(
                "Collector",
                "35999999999",
                "collector",
                true,
                List.of("paper", "glass"),
                "Available on weekdays",
                validAddress()
        );

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void shouldRejectInvalidUpdatePersonFieldsAndNestedAddress() {
        UpdatePersonRequest request = new UpdatePersonRequest(
                " ",
                "1234567890123456",
                "ADMIN",
                false,
                List.of("UNKNOWN"),
                null,
                new AddressRequest(" ", " ", " ", null, " ", " ")
        );

        Set<ConstraintViolation<UpdatePersonRequest>> violations = validator.validate(request);

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains(
                        "name",
                        "telephone",
                        "personType",
                        "garbageType[0].<list element>",
                        "address.cep",
                        "address.street",
                        "address.district",
                        "address.city",
                        "address.state"
                );
    }

    private AddressRequest validAddress() {
        return new AddressRequest(
                "37500-000",
                "Main Street",
                "Downtown",
                "10",
                "Itajuba",
                "Minas Gerais"
        );
    }
}

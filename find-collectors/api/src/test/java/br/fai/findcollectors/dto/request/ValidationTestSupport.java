package br.fai.findcollectors.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

abstract class ValidationTestSupport {

    protected static Validator validator;
    private static ValidatorFactory validatorFactory;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    protected static <T> void assertValid(T value) {
        assertThat(validator.validate(value)).isEmpty();
    }

    protected static <T> void assertViolation(T value, String property, String message) {
        Set<ConstraintViolation<T>> violations = validator.validate(value);

        assertThat(violations)
                .anySatisfy(violation -> {
                    assertThat(violation.getPropertyPath().toString()).isEqualTo(property);
                    assertThat(violation.getMessage()).isEqualTo(message);
                });
    }

    protected static String text(int length) {
        return "x".repeat(length);
    }
}

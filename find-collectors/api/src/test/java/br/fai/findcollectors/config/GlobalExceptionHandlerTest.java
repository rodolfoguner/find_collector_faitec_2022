package br.fai.findcollectors.config;

import br.fai.findcollectors.dto.response.ExceptionResponse;
import br.fai.findcollectors.exceptions.BusinessRuleException;
import br.fai.findcollectors.exceptions.ConflictException;
import br.fai.findcollectors.exceptions.ErrorCode;
import br.fai.findcollectors.exceptions.InvalidCredentialsException;
import br.fai.findcollectors.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = createRequest();

    @Test
    void shouldReturn503ForSessionStorageFailure() {
        var response = new GlobalExceptionHandler().handlePersistenceUnavailable(
                new br.fai.findcollectors.exceptions.AuthenticationUnavailableException(
                        new RuntimeException("password=secret")),
                new org.springframework.mock.web.MockHttpServletRequest("POST", "/api/refresh"));
        org.assertj.core.api.Assertions.assertThat(response.getStatusCode().value()).isEqualTo(503);
        org.assertj.core.api.Assertions.assertThat(response.getBody().code()).isEqualTo("AUTHENTICATION_UNAVAILABLE");
        org.assertj.core.api.Assertions.assertThat(response.getBody().message()).doesNotContain("secret");
    }

    @Test
    void shouldReturnNotFoundForNotFoundException() {
        NotFoundException exception = new NotFoundException(
                ErrorCode.PERSON_NOT_FOUND,
                "Person with id 1 not found"
        );

        Instant beforeHandling = Instant.now();
        ResponseEntity<ExceptionResponse> response = handler.handleNotFound(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertResponse(
                response.getBody(),
                "PERSON_NOT_FOUND",
                "Person with id 1 not found",
                beforeHandling
        );
    }

    @Test
    void shouldReturnConflictForConflictException() {
        ConflictException exception = new ConflictException(
                ErrorCode.EMAIL_ALREADY_REGISTERED,
                "Email already registered"
        );

        Instant beforeHandling = Instant.now();
        ResponseEntity<ExceptionResponse> response = handler.handleConflict(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertResponse(
                response.getBody(),
                "EMAIL_ALREADY_REGISTERED",
                "Email already registered",
                beforeHandling
        );
    }

    @Test
    void shouldReturnUnauthorizedForUnauthorizedException() {
        InvalidCredentialsException exception = new InvalidCredentialsException(
                "Email or password is invalid"
        );

        Instant beforeHandling = Instant.now();
        ResponseEntity<ExceptionResponse> response = handler.handleUnauthorized(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertResponse(
                response.getBody(),
                "INVALID_CREDENTIALS",
                "Email or password is invalid",
                beforeHandling
        );
    }

    @Test
    void shouldReturnUnprocessableEntityForBusinessRuleException() {
        BusinessRuleException exception = new BusinessRuleException(
                "Recycler must be of type recycler."
        );

        Instant beforeHandling = Instant.now();
        ResponseEntity<ExceptionResponse> response = handler.handleUnprocessableEntity(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertResponse(
                response.getBody(),
                "BUSINESS_RULE_VIOLATION",
                "Recycler must be of type recycler.",
                beforeHandling
        );
    }

    @Test
    void shouldNotExposeUnexpectedExceptionDetails() {
        RuntimeException exception = new RuntimeException(
                "password=secret jdbc:postgresql://internal-host/database"
        );

        Instant beforeHandling = Instant.now();
        ResponseEntity<ExceptionResponse> response = handler.handleRuntime(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertResponse(
                response.getBody(),
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                beforeHandling
        );
    }

    private void assertResponse(
            ExceptionResponse response,
            String code,
            String message,
            Instant beforeHandling
    ) {
        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo(code);
        assertThat(response.message()).isEqualTo(message);
        assertThat(response.path()).isEqualTo("/api/persons/1");
        assertThat(response.timestamp()).isBetween(beforeHandling, Instant.now());
    }

    private static MockHttpServletRequest createRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/persons/1");
        return request;
    }
}

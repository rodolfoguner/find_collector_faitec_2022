package br.fai.findcollectors.dto.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class AuthenticationRequestValidationTest extends ValidationTestSupport {

    @Test
    void shouldAcceptValidAuthenticationAndShortLegacyPassword() {
        assertValid(new AuthRequest("person@example.com", "Password1"));
        assertValid(new AuthRequest("person@example.com", "x"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldRejectMissingAuthenticationEmail(String email) {
        assertViolation(new AuthRequest(email, "x"), "email", "email is required");
    }

    @Test
    void shouldRejectInvalidAndOversizedAuthenticationEmail() {
        assertViolation(new AuthRequest("not-an-email", "x"), "email", "email needs to be valid");
        assertViolation(new AuthRequest("x".repeat(64) + "@" + "d".repeat(32) + ".com", "x"),
                "email", "email must have at most 100 characters");
    }

    @Test
    void shouldAcceptAuthenticationEmailAtLimit() {
        assertValid(new AuthRequest("x".repeat(64) + "@" + "d".repeat(31) + ".com", "x"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldRejectMissingAuthenticationPassword(String password) {
        assertViolation(new AuthRequest("person@example.com", password), "password", "password is required");
    }

    @Test
    void shouldEnforceOnlyMaximumPasswordLengthAtLogin() {
        assertValid(new AuthRequest("person@example.com", text(100)));
        assertViolation(new AuthRequest("person@example.com", text(101)),
                "password", "password must have at most 100 characters");
    }

    @Test
    void shouldAcceptRefreshTokenAtLimit() {
        assertValid(new RefreshTokenRequest("t"));
        assertValid(new RefreshTokenRequest(text(4096)));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldRejectMissingRefreshToken(String token) {
        assertViolation(new RefreshTokenRequest(token), "refreshToken", "refreshToken is required");
    }

    @Test
    void shouldRejectRefreshTokenAboveLimit() {
        assertViolation(new RefreshTokenRequest(text(4097)), "refreshToken", "refreshToken is too long");
    }
}

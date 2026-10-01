package br.fai.findcollectors.security;

import br.fai.findcollectors.config.JwtProperties;
import br.fai.findcollectors.config.SecurityConfig;
import br.fai.findcollectors.dto.response.AuthResponse;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.enums.PersonType;
import br.fai.findcollectors.exceptions.InvalidTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenServiceTest {

    private JwtTokenService tokenService;
    private JwtDecoder accessTokenDecoder;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties(
                "test-secret-with-at-least-thirty-two-bytes",
                "find-collectors-test",
                Duration.ofMinutes(15),
                Duration.ofDays(7)
        );
        SecurityConfig securityConfig = new SecurityConfig();
        accessTokenDecoder = securityConfig.accessTokenDecoder(properties);
        tokenService = new JwtTokenService(
                securityConfig.jwtEncoder(properties),
                securityConfig.refreshTokenDecoder(properties),
                properties
        );
    }

    @Test
    void shouldIssueSignedAccessAndRefreshTokensWithoutSensitiveClaims() {
        AuthResponse response = tokenService.issueTokens(person());

        Jwt accessToken = accessTokenDecoder.decode(response.accessToken());

        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(accessToken.getSubject()).isEqualTo("collector@example.com");
        assertThat(accessToken.getClaimAsString("tokenType")).isEqualTo("access");
        assertThat(accessToken.getClaimAsString("personType")).isEqualTo("COLLECTOR");
        assertThat(accessToken.<Number>getClaim("personId").longValue()).isEqualTo(10L);
        assertThat(accessToken.getClaims()).doesNotContainKeys("password", "address", "garbageType");
        assertThat(tokenService.subjectFromRefreshToken(response.refreshToken()))
                .isEqualTo("collector@example.com");
    }

    @Test
    void shouldRejectRefreshTokenAsApiAccessToken() {
        AuthResponse response = tokenService.issueTokens(person());

        assertThatThrownBy(() -> accessTokenDecoder.decode(response.refreshToken()))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void shouldRejectAccessTokenAsRefreshToken() {
        AuthResponse response = tokenService.issueTokens(person());

        assertThatThrownBy(() -> tokenService.subjectFromRefreshToken(response.accessToken()))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Refresh token is invalid or expired");
    }

    private Person person() {
        return Person.builder()
                .id(10L)
                .email("collector@example.com")
                .password("must-not-be-in-token")
                .name("Collector")
                .personType(PersonType.COLLECTOR)
                .build();
    }
}

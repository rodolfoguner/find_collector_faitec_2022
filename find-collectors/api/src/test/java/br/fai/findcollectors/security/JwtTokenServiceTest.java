package br.fai.findcollectors.security;

import br.fai.findcollectors.config.JwtProperties;
import br.fai.findcollectors.config.SecurityConfig;
import br.fai.findcollectors.repositories.AuthSessionRepository;
import br.fai.findcollectors.exceptions.AuthenticationUnavailableException;
import org.springframework.security.authentication.AuthenticationServiceException;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import static org.mockito.Mockito.*;
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
    private AuthSessionRepository sessions;
    private SecurityConfig securityConfig;
    private JwtProperties properties;
    private final UUID sessionId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        properties = new JwtProperties(
                "test-secret-with-at-least-thirty-two-bytes",
                "find-collectors-test",
                Duration.ofMinutes(15),
                Duration.ofDays(7)
        );
        securityConfig = new SecurityConfig();
        sessions = mock(AuthSessionRepository.class);
        when(sessions.isActive(eq(sessionId), eq(10L), any())).thenReturn(true);
        accessTokenDecoder = securityConfig.accessTokenDecoder(properties, sessions, Clock.systemUTC());
        tokenService = new JwtTokenService(
                securityConfig.jwtEncoder(properties),
                securityConfig.refreshTokenDecoder(properties),
                properties
        );
    }

    @Test
    void shouldIssueSignedAccessAndRefreshTokensWithoutSensitiveClaims() {
        IssuedTokens response = issue();

        Jwt accessToken = accessTokenDecoder.decode(response.accessToken());

        assertThat(accessToken.getHeaders().get("alg")).isEqualTo("HS256");
        assertThat(accessToken.getClaimAsString("sid")).isEqualTo(sessionId.toString());
        Jwt refresh = securityConfig.refreshTokenDecoder(properties).decode(response.refreshToken());
        assertThat(refresh.getClaimAsString("sid")).isEqualTo(sessionId.toString());
        assertThat(refresh.getId()).isNotEqualTo(accessToken.getId());
        assertThat(accessToken.getSubject()).isEqualTo("collector@example.com");
        assertThat(accessToken.getClaimAsString("tokenType")).isEqualTo("access");
        assertThat(accessToken.getClaimAsString("personType")).isEqualTo("COLLECTOR");
        assertThat(accessToken.<Number>getClaim("personId").longValue()).isEqualTo(10L);
        assertThat(accessToken.getClaims()).doesNotContainKeys("password", "address", "garbageType");
        assertThat(tokenService.readRefresh(response.refreshToken()).subject())
                .isEqualTo("collector@example.com");
    }

    @Test
    void shouldRejectRefreshTokenAsApiAccessToken() {
        IssuedTokens response = issue();

        assertThatThrownBy(() -> accessTokenDecoder.decode(response.refreshToken()))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void shouldRejectAccessTokenAsRefreshToken() {
        IssuedTokens response = issue();

        assertThatThrownBy(() -> tokenService.readRefresh(response.accessToken()))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Refresh token is invalid or expired");
    }

    @Test
    void shouldRejectRevokedSession() {
        IssuedTokens response = issue();
        when(sessions.isActive(eq(sessionId), eq(10L), any())).thenReturn(false);
        assertThatThrownBy(() -> accessTokenDecoder.decode(response.accessToken())).isInstanceOf(JwtException.class);
    }

    @Test
    void shouldFailClosedWhenSessionStoreIsUnavailable() {
        IssuedTokens response = issue();
        when(sessions.isActive(eq(sessionId), eq(10L), any()))
                .thenThrow(new AuthenticationUnavailableException(new RuntimeException("database down")));
        assertThatThrownBy(() -> accessTokenDecoder.decode(response.accessToken()))
                .isInstanceOf(AuthenticationServiceException.class);
    }

    @Test
    void shouldRejectLegacyTokensWithoutSessionClaimsAndExpiredTokens() {
        Instant now = Instant.now();
        var claims = org.springframework.security.oauth2.jwt.JwtClaimsSet.builder()
                .issuer(properties.issuer()).subject("collector@example.com")
                .issuedAt(now).expiresAt(now.plusSeconds(60)).claim("tokenType", "access").build();
        String legacy = securityConfig.jwtEncoder(properties).encode(
                org.springframework.security.oauth2.jwt.JwtEncoderParameters.from(
                        org.springframework.security.oauth2.jwt.JwsHeader.with(
                                org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256).build(), claims))
                .getTokenValue();
        assertThatThrownBy(() -> accessTokenDecoder.decode(legacy)).isInstanceOf(JwtException.class);
        IssuedTokens expired = tokenService.issue(person(), sessionId, now.minusSeconds(300),
                now.minusSeconds(120), now.minusSeconds(120));
        assertThatThrownBy(() -> accessTokenDecoder.decode(expired.accessToken())).isInstanceOf(JwtException.class);
    }

    private IssuedTokens issue() {
        Instant now = Instant.now();
        return tokenService.issue(person(), sessionId, now, now.plusSeconds(900), now.plusSeconds(604800));
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

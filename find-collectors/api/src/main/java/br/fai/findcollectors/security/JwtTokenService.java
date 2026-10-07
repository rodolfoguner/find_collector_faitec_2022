package br.fai.findcollectors.security;

import br.fai.findcollectors.config.JwtProperties;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.exceptions.InvalidTokenException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class JwtTokenService implements SessionTokenCodec {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder refreshTokenDecoder;
    private final String issuer;

    public JwtTokenService(JwtEncoder jwtEncoder,
                           @Qualifier("refreshTokenDecoder") JwtDecoder refreshTokenDecoder,
                           JwtProperties properties) {
        this.jwtEncoder = jwtEncoder;
        this.refreshTokenDecoder = refreshTokenDecoder;
        this.issuer = properties.issuer();
    }

    @Override
    public IssuedTokens issue(Person person, UUID sessionId, Instant issuedAt,
                              Instant accessExpiresAt, Instant sessionExpiresAt) {
        return new IssuedTokens(
                encode(person, sessionId, "access", issuedAt, accessExpiresAt), accessExpiresAt,
                encode(person, sessionId, "refresh", issuedAt, sessionExpiresAt), sessionExpiresAt);
    }

    @Override
    public RefreshTokenIdentity readRefresh(String token) {
        try {
            Jwt jwt = refreshTokenDecoder.decode(token);
            return new RefreshTokenIdentity(UUID.fromString(jwt.getClaimAsString("sid")),
                    ((Number) jwt.getClaim("personId")).longValue(), jwt.getSubject());
        } catch (JwtException | IllegalArgumentException exception) {
            throw new InvalidTokenException("Refresh token is invalid or expired");
        }
    }

    private String encode(Person person, UUID sessionId, String tokenType, Instant issuedAt, Instant expiresAt) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(person.getEmail())
                .id(UUID.randomUUID().toString())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .claim("sid", sessionId.toString())
                .claim("personId", person.getId())
                .claim("personType", person.getPersonType().name())
                .claim("tokenType", tokenType)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
    }
}

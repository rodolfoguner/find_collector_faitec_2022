package br.fai.findcollectors.security;

import br.fai.findcollectors.config.JwtProperties;
import br.fai.findcollectors.dto.response.AuthResponse;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.exceptions.InvalidTokenException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder refreshTokenDecoder;
    private final JwtProperties properties;

    public JwtTokenService(
            JwtEncoder jwtEncoder,
            @Qualifier("refreshTokenDecoder") JwtDecoder refreshTokenDecoder,
            JwtProperties properties
    ) {
        this.jwtEncoder = jwtEncoder;
        this.refreshTokenDecoder = refreshTokenDecoder;
        this.properties = properties;
    }

    public AuthResponse issueTokens(Person person) {
        Instant issuedAt = Instant.now();
        Instant accessTokenExpiresAt = issuedAt.plus(properties.accessTokenTtl());
        Instant refreshTokenExpiresAt = issuedAt.plus(properties.refreshTokenTtl());

        return new AuthResponse(
                "Bearer",
                encode(person, "access", issuedAt, accessTokenExpiresAt),
                accessTokenExpiresAt,
                encode(person, "refresh", issuedAt, refreshTokenExpiresAt),
                refreshTokenExpiresAt
        );
    }

    public String subjectFromRefreshToken(String refreshToken) {
        try {
            Jwt jwt = refreshTokenDecoder.decode(refreshToken);
            return jwt.getSubject();
        } catch (JwtException exception) {
            throw new InvalidTokenException("Refresh token is invalid or expired");
        }
    }

    private String encode(Person person, String tokenType, Instant issuedAt, Instant expiresAt) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(properties.issuer())
                .subject(person.getEmail())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .claim("personId", person.getId())
                .claim("personType", person.getPersonType().name())
                .claim("tokenType", tokenType)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}

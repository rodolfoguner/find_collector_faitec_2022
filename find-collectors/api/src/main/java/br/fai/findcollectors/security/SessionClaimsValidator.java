package br.fai.findcollectors.security;

import br.fai.findcollectors.enums.PersonType;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.UUID;

public class SessionClaimsValidator implements OAuth2TokenValidator<Jwt> {
    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        try {
            UUID.fromString(token.getClaimAsString("sid"));
            UUID.fromString(token.getId());
            Object id = token.getClaim("personId");
            if (!(id instanceof Number number) || number.longValue() <= 0
                    || number.doubleValue() != number.longValue()
                    || token.getSubject() == null || token.getSubject().isBlank()
                    || token.getExpiresAt() == null || token.getIssuedAt() == null
                    || !token.getExpiresAt().isAfter(token.getIssuedAt())) {
                return invalid();
            }
            PersonType.valueOf(token.getClaimAsString("personType"));
            return OAuth2TokenValidatorResult.success();
        } catch (IllegalArgumentException | NullPointerException exception) {
            return invalid();
        }
    }

    private OAuth2TokenValidatorResult invalid() {
        return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Invalid session claims", null));
    }
}

package br.fai.findcollectors.security;

import br.fai.findcollectors.dto.response.ExceptionResponse;
import br.fai.findcollectors.exceptions.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        boolean unavailable = authException instanceof AuthenticationServiceException;
        boolean invalid = authException instanceof InvalidBearerTokenException;
        response.setStatus(unavailable ? HttpServletResponse.SC_SERVICE_UNAVAILABLE : HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        if (!unavailable) {
            response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
        }
        objectMapper.writeValue(response.getOutputStream(), new ExceptionResponse(
                (unavailable ? ErrorCode.AUTHENTICATION_UNAVAILABLE
                        : invalid ? ErrorCode.INVALID_TOKEN : ErrorCode.AUTHENTICATION_REQUIRED).name(),
                unavailable ? "Authentication is temporarily unavailable"
                        : invalid ? "Token is invalid, expired or revoked" : "Authentication is required",
                request.getRequestURI(),
                Instant.now()
        ));
    }
}

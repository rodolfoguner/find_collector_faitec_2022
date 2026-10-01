package br.fai.findcollectors.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThat;

class RestSecurityHandlerTest {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void shouldReturnStandardUnauthorizedResponse() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/me");
        MockHttpServletResponse response = new MockHttpServletResponse();

        new RestAuthenticationEntryPoint(objectMapper).commence(request, response, null);

        JsonNode body = objectMapper.readTree(response.getContentAsByteArray());
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getHeader(HttpHeaders.WWW_AUTHENTICATE)).isEqualTo("Bearer");
        assertThat(body.get("code").asText()).isEqualTo("AUTHENTICATION_REQUIRED");
        assertThat(body.get("message").asText()).isEqualTo("Authentication is required");
        assertThat(body.get("path").asText()).isEqualTo("/api/me");
    }

    @Test
    void shouldReturnStandardForbiddenResponse() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("DELETE", "/api/person/2");
        MockHttpServletResponse response = new MockHttpServletResponse();

        new RestAccessDeniedHandler(objectMapper).handle(
                request,
                response,
                new AccessDeniedException("must not be exposed")
        );

        JsonNode body = objectMapper.readTree(response.getContentAsByteArray());
        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(body.get("code").asText()).isEqualTo("ACCESS_DENIED");
        assertThat(body.get("message").asText()).isEqualTo("Access is denied");
        assertThat(body.get("path").asText()).isEqualTo("/api/person/2");
    }
}

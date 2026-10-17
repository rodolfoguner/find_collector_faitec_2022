package br.fai.findcollectors.controller;

import br.fai.findcollectors.config.GlobalExceptionHandler;
import br.fai.findcollectors.config.SecurityConfig;
import br.fai.findcollectors.security.RestAccessDeniedHandler;
import br.fai.findcollectors.security.RestAuthenticationEntryPoint;
import br.fai.findcollectors.usecases.auth.AuthSessionService;
import br.fai.findcollectors.usecases.auth.AuthenticateUserUseCase;
import br.fai.findcollectors.usecases.collect.CollectQueryUseCase;
import br.fai.findcollectors.usecases.collect.CreateCollectUseCase;
import br.fai.findcollectors.usecases.collect.DeleteCollectUseCase;
import br.fai.findcollectors.usecases.collect.UpdateCollectUseCase;
import br.fai.findcollectors.usecases.person.CreatePersonUseCase;
import br.fai.findcollectors.usecases.person.DeletePersonUseCase;
import br.fai.findcollectors.usecases.person.PersonQueryUseCase;
import br.fai.findcollectors.usecases.person.UpdatePersonUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AccountRestController.class, PersonRestController.class, CollectRestController.class})
@Import({SecurityConfig.class, RestAuthenticationEntryPoint.class, RestAccessDeniedHandler.class,
        GlobalExceptionHandler.class})
class ControllerSecurityTest {

    private static final UUID SESSION_ID = UUID.fromString("643735be-8f22-4d54-b46f-ae2dc35bd25f");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean(name = "accessTokenDecoder")
    private JwtDecoder accessTokenDecoder;

    @MockitoBean
    private AuthenticateUserUseCase authenticateUserUseCase;
    @MockitoBean
    private AuthSessionService authSessionService;
    @MockitoBean
    private CreatePersonUseCase createPersonUseCase;
    @MockitoBean
    private PersonQueryUseCase personQueryUseCase;
    @MockitoBean
    private UpdatePersonUseCase updatePersonUseCase;
    @MockitoBean
    private DeletePersonUseCase deletePersonUseCase;
    @MockitoBean
    private CollectQueryUseCase collectQueryUseCase;
    @MockitoBean
    private CreateCollectUseCase createCollectUseCase;
    @MockitoBean
    private UpdateCollectUseCase updateCollectUseCase;
    @MockitoBean
    private DeleteCollectUseCase deleteCollectUseCase;
    @MockitoBean(name = "jpaMappingContext")
    private JpaMetamodelMappingContext jpaMappingContext;

    @BeforeEach
    void configureTokens() {
        when(accessTokenDecoder.decode("valid-access")).thenReturn(accessJwt());
        when(accessTokenDecoder.decode("invalid-access")).thenThrow(new BadJwtException("signature details"));
        when(accessTokenDecoder.decode("refresh-token")).thenThrow(new BadJwtException("wrong token type"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/login", "/api/signup", "/api/refresh"})
    void accountEntryPointsArePublic(String path) throws Exception {
        mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/me", "/api/person", "/api/collect"})
    void protectedGetEndpointsRejectAnonymousRequests(String path) throws Exception {
        assertSecurityError(get(path), "AUTHENTICATION_REQUIRED", path);
    }

    @Test
    void logoutRejectsAnonymousRequest() throws Exception {
        assertSecurityError(post("/api/logout"), "AUTHENTICATION_REQUIRED", "/api/logout");
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/me", "/api/person", "/api/collect"})
    void protectedEndpointsAcceptAValidAccessBearer(String path) throws Exception {
        if (path.equals("/api/me")) {
            when(personQueryUseCase.findPersonByEmail("collector@example.com"))
                    .thenReturn(ControllerTestFixtures.collector(42L));
        } else if (path.equals("/api/person")) {
            when(personQueryUseCase.find()).thenReturn(List.of());
        } else {
            when(collectQueryUseCase.find()).thenReturn(List.of());
        }

        mockMvc.perform(get(path).header(HttpHeaders.AUTHORIZATION, "Bearer valid-access"))
                .andExpect(status().isOk());
    }

    @Test
    void logoutAcceptsAValidAccessBearer() throws Exception {
        mockMvc.perform(post("/api/logout").header(HttpHeaders.AUTHORIZATION, "Bearer valid-access"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        verify(authSessionService).logout(SESSION_ID, 42L);
    }

    @Test
    void invalidBearerReturnsStandardInvalidTokenResponse() throws Exception {
        assertSecurityError(
                get("/api/person").header(HttpHeaders.AUTHORIZATION, "Bearer invalid-access"),
                "INVALID_TOKEN", "/api/person");
    }

    @Test
    void refreshTokenIsNotAcceptedAsAccessBearer() throws Exception {
        assertSecurityError(
                get("/api/collect").header(HttpHeaders.AUTHORIZATION, "Bearer refresh-token"),
                "INVALID_TOKEN", "/api/collect");
    }

    private Jwt accessJwt() {
        Instant issuedAt = Instant.parse("2026-10-07T12:00:00Z");
        return Jwt.withTokenValue("valid-access")
                .header("alg", "HS256")
                .issuer("find-collectors")
                .subject("collector@example.com")
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plusSeconds(900))
                .claim("jti", "41a98a67-017c-45e3-96df-7ec42d311e17")
                .claim("tokenType", "access")
                .claim("sid", SESSION_ID.toString())
                .claim("personId", 42L)
                .claim("personType", "COLLECTOR")
                .build();
    }

    private void assertSecurityError(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder request,
            String code,
            String path
    ) throws Exception {
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, "Bearer"))
                .andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value(path))
                .andExpect(jsonPath("$.timestamp", matchesPattern(
                        "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?Z$")))
                .andExpect(jsonPath("$.exception").doesNotExist())
                .andExpect(jsonPath("$.stackTrace").doesNotExist());
    }
}

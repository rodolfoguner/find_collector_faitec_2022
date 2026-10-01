package br.fai.findcollectors.config;

import br.fai.findcollectors.security.RestAccessDeniedHandler;
import br.fai.findcollectors.security.RestAuthenticationEntryPoint;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

    private static final String TOKEN_TYPE_CLAIM = "tokenType";

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            @Qualifier("accessTokenDecoder") JwtDecoder accessTokenDecoder,
            RestAuthenticationEntryPoint authenticationEntryPoint,
            RestAccessDeniedHandler accessDeniedHandler
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/login", "/api/signup", "/api/refresh").permitAll()
                        .requestMatchers(
                                "/actuator/health",
                                "/actuator/info",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .jwt(jwt -> jwt
                                .decoder(accessTokenDecoder)
                                .jwtAuthenticationConverter(source -> new JwtAuthenticationToken(
                                        source,
                                        List.of(new SimpleGrantedAuthority(
                                                "ROLE_" + source.getClaimAsString("personType")
                                        )),
                                        source.getSubject()
                                ))
                        )
                );

        return http.build();
    }

    @Bean
    public JwtEncoder jwtEncoder(JwtProperties properties) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey(properties)));
    }

    @Bean("accessTokenDecoder")
    @Primary
    public JwtDecoder accessTokenDecoder(JwtProperties properties) {
        return tokenDecoder(properties, "access");
    }

    @Bean("refreshTokenDecoder")
    public JwtDecoder refreshTokenDecoder(JwtProperties properties) {
        return tokenDecoder(properties, "refresh");
    }

    private JwtDecoder tokenDecoder(JwtProperties properties, String expectedTokenType) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey(secretKey(properties))
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(properties.issuer()),
                new JwtClaimValidator<>(TOKEN_TYPE_CLAIM, expectedTokenType::equals)
        ));

        return decoder;
    }

    private SecretKey secretKey(JwtProperties properties) {
        byte[] secret = properties.secret().getBytes(StandardCharsets.UTF_8);
        if (secret.length < 32) {
            throw new IllegalStateException("JWT secret must contain at least 32 bytes");
        }
        return new SecretKeySpec(secret, "HmacSHA256");
    }
}

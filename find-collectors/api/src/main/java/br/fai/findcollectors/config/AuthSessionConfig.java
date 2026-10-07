package br.fai.findcollectors.config;

import br.fai.findcollectors.repositories.AuthSessionRepository;
import br.fai.findcollectors.repositories.PersonRepository;
import br.fai.findcollectors.security.SessionTokenCodec;
import br.fai.findcollectors.usecases.auth.AuthSessionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Clock;

@Configuration
@EnableScheduling
public class AuthSessionConfig {
    @Bean
    public Clock authClock() {
        return Clock.systemUTC();
    }

    @Bean
    public AuthSessionService authSessionService(AuthSessionRepository sessions, PersonRepository persons,
                                                SessionTokenCodec tokens, Clock authClock, JwtProperties properties) {
        return new AuthSessionService(sessions, persons, tokens, authClock,
                properties.accessTokenTtl(), properties.refreshTokenTtl());
    }
}

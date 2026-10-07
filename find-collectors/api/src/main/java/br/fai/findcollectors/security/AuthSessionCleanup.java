package br.fai.findcollectors.security;

import br.fai.findcollectors.repositories.AuthSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.Clock;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthSessionCleanup {
    private final AuthSessionRepository sessions;
    private final Clock authClock;

    @Scheduled(fixedDelayString = "${security.sessions.cleanup-interval:PT1H}",
               initialDelayString = "${security.sessions.cleanup-interval:PT1H}")
    public void deleteExpired() {
        int deleted = sessions.deleteExpired(authClock.instant());
        if (deleted > 0) {
            log.info("Removed {} expired authentication sessions", deleted);
        }
    }
}

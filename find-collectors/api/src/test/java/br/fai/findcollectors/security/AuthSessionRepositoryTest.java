package br.fai.findcollectors.security;

import br.fai.findcollectors.exceptions.AuthenticationUnavailableException;
import br.fai.findcollectors.persistence.jpa.entities.AuthSessionEntity;
import br.fai.findcollectors.persistence.jpa.entities.RefreshTokenEntity;
import br.fai.findcollectors.persistence.repositories.AuthSessionRepositoryImpl;
import br.fai.findcollectors.repositories.AuthSessionRepository.RotationResult;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.SimpleTransactionStatus;
import java.time.*;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthSessionRepositoryTest {
    private final EntityManager em = mock(EntityManager.class);
    private final PlatformTransactionManager transactions = mock(PlatformTransactionManager.class);
    private final SimpleTransactionStatus status = new SimpleTransactionStatus();
    private final Instant now = Instant.parse("2026-10-05T12:00:00Z");
    private final UUID id = UUID.randomUUID();
    private final AuthSessionEntity session = new AuthSessionEntity();
    private final RefreshTokenEntity refresh = new RefreshTokenEntity();
    private AuthSessionRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        when(transactions.getTransaction(any())).thenReturn(status);
        repository = new AuthSessionRepositoryImpl(em, transactions, Clock.fixed(now, ZoneOffset.UTC));
        session.setId(id);
        session.setPersonId(10L);
        session.setCreatedAt(now.minusSeconds(60));
        session.setExpiresAt(now.plusSeconds(600));
        refresh.setSessionId(id);
        refresh.setTokenHash("current");
        when(em.find(AuthSessionEntity.class, id, LockModeType.PESSIMISTIC_WRITE)).thenReturn(session);
        when(em.find(RefreshTokenEntity.class, "current")).thenReturn(refresh);
    }

    @Test
    void replayShouldCommitRevocationInsteadOfRollingBack() {
        refresh.setConsumedAt(now.minusSeconds(1));
        assertThat(repository.rotate(id, 10L, "current", "next", now)).isEqualTo(RotationResult.REUSED);
        assertThat(session.getRevokedAt()).isEqualTo(now);
        verify(transactions).commit(status);
        verify(transactions, never()).rollback(any());
        verify(em, never()).persist(any());
    }

    @Test
    void rotationShouldRecheckExpirationAfterAcquiringLock() {
        session.setExpiresAt(now.minusSeconds(1));
        assertThat(repository.rotate(id, 10L, "current", "next", now.minusSeconds(10)))
                .isEqualTo(RotationResult.INVALID);
        assertThat(refresh.getConsumedAt()).isNull();
        verify(em, never()).persist(any());
    }

    @Test
    void unknownRefreshShouldNotRevokeSession() {
        assertThat(repository.rotate(id, 10L, "unknown", "next", now)).isEqualTo(RotationResult.INVALID);
        assertThat(session.getRevokedAt()).isNull();
    }

    @Test
    void commitFailureShouldBeReportedAsAuthenticationUnavailable() {
        doThrow(new TransactionSystemException("database details")).when(transactions).commit(status);
        assertThatThrownBy(() -> repository.revoke(id, 10L, now))
                .isInstanceOf(AuthenticationUnavailableException.class)
                .hasMessage("Authentication is temporarily unavailable");
    }
}

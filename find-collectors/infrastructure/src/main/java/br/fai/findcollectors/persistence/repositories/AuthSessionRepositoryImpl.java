package br.fai.findcollectors.persistence.repositories;

import br.fai.findcollectors.exceptions.AuthenticationUnavailableException;
import br.fai.findcollectors.persistence.jpa.entities.AuthSessionEntity;
import br.fai.findcollectors.persistence.jpa.entities.RefreshTokenEntity;
import br.fai.findcollectors.repositories.AuthSessionRepository;
import br.fai.findcollectors.security.AuthSession;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Repository
public class AuthSessionRepositoryImpl implements AuthSessionRepository {
    private final EntityManager entityManager;
    private final TransactionTemplate writeTransaction;
    private final TransactionTemplate readTransaction;
    private final Clock clock;

    public AuthSessionRepositoryImpl(EntityManager entityManager, PlatformTransactionManager transactions, Clock clock) {
        this.entityManager = entityManager;
        this.clock = clock;
        this.writeTransaction = new TransactionTemplate(transactions);
        this.readTransaction = new TransactionTemplate(transactions);
        this.readTransaction.setReadOnly(true);
    }

    private <T> T execute(TransactionTemplate transaction, Supplier<T> operation) {
        try {
            return transaction.execute(status -> operation.get());
        } catch (DataAccessException | TransactionException | PersistenceException exception) {
            // Includes commit failures, not just failures inside the operation.
            throw new AuthenticationUnavailableException(exception);
        }
    }

    private AuthSession toSession(AuthSessionEntity entity) {
        return new AuthSession(entity.getId(), entity.getPersonId(), entity.getCreatedAt(),
                entity.getExpiresAt(), entity.getRevokedAt());
    }

    @Override
    public void create(AuthSession session, String refreshHash) {
        execute(writeTransaction, () -> {
            AuthSessionEntity entity = new AuthSessionEntity();
            entity.setId(session.id());
            entity.setPersonId(session.personId());
            entity.setCreatedAt(session.createdAt());
            entity.setExpiresAt(session.expiresAt());
            entityManager.persist(entity);
            addRefresh(session.id(), refreshHash);

            return null;
        });
    }

    @Override
    public Optional<AuthSession> findById(UUID id) {
        return execute(readTransaction, () -> Optional.ofNullable(entityManager.find(AuthSessionEntity.class, id))
                .map(this::toSession));
    }

    @Override
    public boolean isActive(UUID id, Long personId, Instant now) {
        return execute(readTransaction, () -> entityManager.createQuery("""
                    select count(s) from AuthSessionEntity s
                    where s.id = :id and s.personId = :personId
                      and s.revokedAt is null and s.expiresAt > :now
                    """, Long.class)
                    .setParameter("id", id).setParameter("personId", personId).setParameter("now", now)
                    .getSingleResult() == 1);
    }

    @Override
    public RotationResult rotate(UUID id, Long personId, String currentHash, String nextHash, Instant now) {
        return execute(writeTransaction, () -> {
            AuthSessionEntity session = entityManager.find(AuthSessionEntity.class, id, LockModeType.PESSIMISTIC_WRITE);
            if (session == null) {
                return RotationResult.INVALID;
            }
            Instant checkedAt = clock.instant();
            RefreshTokenEntity current = entityManager.find(RefreshTokenEntity.class, currentHash);
            RotationResult decision = toSession(session).decideRotation(personId, checkedAt,
                    current != null && id.equals(current.getSessionId()),
                    current == null ? null : current.getConsumedAt());
            if (decision == RotationResult.REUSED) {
                session.setRevokedAt(checkedAt);
            }
            if (decision != RotationResult.ROTATED) {
                return decision;
            }
            current.setConsumedAt(checkedAt);
            addRefresh(id, nextHash);
            return RotationResult.ROTATED;

        });
    }

    @Override
    public void revoke(UUID id, Long personId, Instant now) {
        execute(writeTransaction, () -> {
            AuthSessionEntity session = entityManager.find(AuthSessionEntity.class, id, LockModeType.PESSIMISTIC_WRITE);
            if (session != null && personId.equals(session.getPersonId()) && session.getRevokedAt() == null) {
                session.setRevokedAt(now);
            }

            return null;
        });
    }

    @Override
    public int deleteExpired(Instant now) {
        // The database cascades deletion to the refresh history.
        return execute(writeTransaction, () -> entityManager
                .createQuery("delete from AuthSessionEntity s where s.expiresAt <= :now")
                .setParameter("now", now).executeUpdate());
    }

    private void addRefresh(UUID sessionId, String hash) {
        RefreshTokenEntity refresh = new RefreshTokenEntity();
        refresh.setTokenHash(hash);
        refresh.setSessionId(sessionId);
        entityManager.persist(refresh);
    }
}

package pl.sparkbit.security.dao;

import pl.sparkbit.security.domain.Session;

import java.time.Instant;
import java.util.Optional;

public interface SessionDao {

    void insertSession(Session session);

    void updateSessionExpiryTs(String authToken, Instant expiresAt);

    Optional<Session> selectSession(String authToken);

    void deleteSession(String authToken, Instant deletedTs);

    void deleteSessionsMarkedAsDeleted(Instant olderThan);

    void markSessionsAsDeleted(String userId, Instant deletedTs);

    void deleteExpiredSessions(Instant now);
}

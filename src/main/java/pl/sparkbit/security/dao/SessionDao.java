package pl.sparkbit.security.dao;

import pl.sparkbit.security.domain.Session;

import java.time.Instant;
import java.util.Optional;

public interface SessionDao {

    void insertSession(Session session);

    void updateSessionExpirationTimestamp(String authTokenHash, Instant expirationTimestamp);

    void updateExtraAuthnCheckRequired(String authTokenHash, boolean value);

    Optional<Session> selectSession(String authTokenHash);

    void deleteSession(String authTokenHash, Instant deletionTimestamp);

    void deleteSessions(String userId, Instant deletionTimestamp);

    void deleteExpiredSessions(Instant now);

    void purgeDeletedSessions(Instant deletedBefore);
}

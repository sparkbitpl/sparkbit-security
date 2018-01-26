package pl.sparkbit.security.dao;

import pl.sparkbit.security.domain.Session;

import java.time.Instant;
import java.util.Optional;

public interface SessionDao {

    void insertSession(Session session);

    Optional<Session> selectSession(String authToken);

    void deleteSession(String authToken, Instant deletedTs);

    void deleteSessions(Instant olderThan);

    void deleteSessions(String userId, Instant deletedTs);
}

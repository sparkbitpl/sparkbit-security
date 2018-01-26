package pl.sparkbit.security.dao;

import pl.sparkbit.security.domain.Session;
import pl.sparkbit.security.login.AuthnAttributes;
import pl.sparkbit.security.login.LoginUserDetails;

import java.time.Instant;
import java.util.Optional;

public interface SessionDao {

    void insertSession(Session session);

    Optional<Session> selectSession(String authToken);

    Optional<String> selectUserId(AuthnAttributes authnAttributes);

    Optional<LoginUserDetails> selectLoginUserDetails(String userId);

    void deleteSession(String authToken, Instant deletedTs);

    void deleteSessions(Instant olderThan);

    void deleteSessions(String userId, Instant deletedTs);
}

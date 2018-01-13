package pl.sparkbit.security.session.dao;

import pl.sparkbit.security.session.auth.AuthnAttributes;
import pl.sparkbit.security.session.auth.LoginUserDetails;
import pl.sparkbit.security.session.domain.Session;

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

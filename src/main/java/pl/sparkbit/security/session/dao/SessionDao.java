package pl.sparkbit.security.session.dao;

import pl.sparkbit.security.session.auth.AuthnAttributes;
import pl.sparkbit.security.session.auth.LoginUserDetails;
import pl.sparkbit.security.session.domain.Session;

import java.time.Instant;
import java.util.Optional;

public interface SessionDao {

    void insertSession(Session session);

    Optional<Session> selectSession(String authToken);

    Optional<LoginUserDetails> selectLoginUserDetails(AuthnAttributes authnAttributes);

    void deleteSession(String authToken, Instant deletedTs);

    void deleteSessions(Instant olderThan);
}

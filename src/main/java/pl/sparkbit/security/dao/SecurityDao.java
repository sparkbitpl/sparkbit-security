package pl.sparkbit.security.dao;

import pl.sparkbit.security.domain.Session;
import pl.sparkbit.security.login.AuthnAttributes;
import pl.sparkbit.security.login.LoginUserDetails;
import pl.sparkbit.security.rest.RestUserDetails;

import java.util.Optional;

public interface SecurityDao {

    void insertSession(Session session);

    Optional<LoginUserDetails> selectLoginUserDetails(AuthnAttributes authnAttributes);

    Optional<RestUserDetails> selectRestUserDetails(String authToken);

    Optional<Session> selectSession(String authToken);

    void deleteSession(String authToken);
}

package pl.sparkbit.security.dao;

import pl.sparkbit.security.domain.RestUserDetails;
import pl.sparkbit.security.login.AuthnAttributes;
import pl.sparkbit.security.login.LoginUserDetails;

import java.util.Optional;

public interface UserDetailsDao {

    Optional<LoginUserDetails> selectLoginUserDetails(String userId);

    Optional<RestUserDetails> selectRestUserDetails(String authToken);

    Optional<String> selectUserId(AuthnAttributes authnAttributes);
}
package pl.sparkbit.security.dao;

import pl.sparkbit.security.rest.user.RestUserDetails;

import java.util.Optional;

public interface SecurityDao {

    Optional<RestUserDetails> selectRestUserDetails(String authToken);
}

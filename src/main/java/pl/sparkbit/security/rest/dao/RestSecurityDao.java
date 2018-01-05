package pl.sparkbit.security.rest.dao;

import pl.sparkbit.security.rest.domain.RestUserDetails;

import java.util.Optional;

public interface RestSecurityDao {

    Optional<RestUserDetails> selectRestUserDetails(String authToken);
}

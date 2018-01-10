package pl.sparkbit.security.rest.dao;

import pl.sparkbit.security.rest.domain.RestUserDetails;

import java.util.Optional;

@SuppressWarnings("unused")
public interface RestSecurityDao {

    void insertUserRole(String userId, String role);

    Optional<RestUserDetails> selectRestUserDetails(String authToken);

    String selectPasswordHashForUser(String userId);

    void updatePassword(String userId, String password);
}

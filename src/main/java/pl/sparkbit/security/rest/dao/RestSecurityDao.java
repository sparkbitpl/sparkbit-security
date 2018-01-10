package pl.sparkbit.security.rest.dao;

import pl.sparkbit.security.rest.domain.RestUserDetails;

import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unused")
public interface RestSecurityDao {

    void insertCredentials(String userId, String password, boolean enabled, boolean deleted, Map authnAttributes);

    void insertUserRole(String userId, String role);

    Optional<RestUserDetails> selectRestUserDetails(String authToken);

    String selectPasswordHashForUser(String userId);

    void updatePassword(String userId, String password);
}

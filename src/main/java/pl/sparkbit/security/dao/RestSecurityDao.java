package pl.sparkbit.security.dao;

import org.springframework.security.core.GrantedAuthority;
import pl.sparkbit.security.domain.Credentials;
import pl.sparkbit.security.domain.RestUserDetails;

import java.util.Optional;

@SuppressWarnings("unused")
public interface RestSecurityDao {

    void insertCredentials(Credentials credentials);

    void insertUserRole(String userId, GrantedAuthority role);

    Optional<RestUserDetails> selectRestUserDetails(String authToken);

    String selectPasswordHashForUser(String userId);

    void updateCredentials(Credentials credentials);
}

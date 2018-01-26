package pl.sparkbit.security.dao;

import org.springframework.security.core.GrantedAuthority;
import pl.sparkbit.security.domain.Credentials;

import java.util.Optional;

public interface CredentialsDao {

    void insertCredentials(Credentials credentials);

    void insertUserRole(String userId, GrantedAuthority role);

    Optional<String> selectPasswordHashForUser(String userId);

    void updateCredentials(Credentials credentials);
}

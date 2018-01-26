package pl.sparkbit.security.service;

import org.springframework.security.core.GrantedAuthority;
import pl.sparkbit.security.domain.Credentials;

import java.util.Collection;

@SuppressWarnings("unused")
public interface CredentialsService {

    void insertCredentials(Credentials credentials, GrantedAuthority role);

    void insertCredentials(Credentials credentials, Collection<GrantedAuthority> roles);

    void disableUser(String userId);

    void enableUser(String userId);

    void deleteUser(String userId);
}

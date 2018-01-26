package pl.sparkbit.security.service;

import org.springframework.security.core.GrantedAuthority;
import pl.sparkbit.security.domain.Credentials;
import pl.sparkbit.security.domain.RestUserDetails;
import pl.sparkbit.security.mvc.dto.in.ChangePasswordDTO;

import java.util.Collection;

@SuppressWarnings("unused")
public interface RestSecurityService {

    void insertCredentials(Credentials credentials, GrantedAuthority role);

    void insertCredentials(Credentials credentials, Collection<GrantedAuthority> roles);

    RestUserDetails retrieveRestUserDetails(String authToken);

    void changeCurrentUserPassword(ChangePasswordDTO dto);

    void disableUser(String userId);

    void enableUser(String userId);

    void deleteUser(String userId);
}

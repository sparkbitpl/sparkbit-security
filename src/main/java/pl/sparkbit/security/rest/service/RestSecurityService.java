package pl.sparkbit.security.rest.service;

import org.springframework.security.core.GrantedAuthority;
import pl.sparkbit.security.rest.domain.Credentials;
import pl.sparkbit.security.rest.domain.RestUserDetails;
import pl.sparkbit.security.rest.mvc.dto.in.ChangePasswordDTO;

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

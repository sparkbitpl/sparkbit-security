package pl.sparkbit.security.rest.service;

import pl.sparkbit.security.rest.domain.RestUserDetails;
import pl.sparkbit.security.rest.mvc.dto.in.ChangePasswordDTO;

import java.util.Collection;
import java.util.Map;

@SuppressWarnings("unused")
public interface RestSecurityService {

    void insertCredentialsAndRoles(String userId, String email, String password, boolean enabled,
                                   boolean deleted, Collection<String> roles);

    void insertCredentialsAndRoles(String userId, String password, boolean enabled, boolean deleted,
                                   Map<String, String> authnAttributes, Collection<String> roles);

    RestUserDetails retrieveRestUserDetails(String authToken);

    void changeCurrentUserPassword(ChangePasswordDTO dto);
}

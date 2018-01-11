package pl.sparkbit.security.rest.service;

import pl.sparkbit.security.challenge.domain.Credentials;
import pl.sparkbit.security.rest.domain.RestUserDetails;
import pl.sparkbit.security.rest.mvc.dto.in.ChangePasswordDTO;

import java.util.Collection;

@SuppressWarnings("unused")
public interface RestSecurityService {

    void insertCredentialsAndRoles(Credentials credentials, Collection<String> roles);

    RestUserDetails retrieveRestUserDetails(String authToken);

    void changeCurrentUserPassword(ChangePasswordDTO dto);
}

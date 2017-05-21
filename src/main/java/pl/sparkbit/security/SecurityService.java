package pl.sparkbit.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import pl.sparkbit.security.domain.Session;
import pl.sparkbit.security.rest.RestUserDetails;

public interface SecurityService extends UserDetailsService {

    Session startNewSession(String oldAuthToken);

    RestUserDetails retrieveRestUserDetails(String authToken);

    void logout();
}

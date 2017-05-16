package pl.sparkbit.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import pl.sparkbit.security.domain.Session;
import pl.sparkbit.security.rest.RestUserDetails;

public interface SecurityService extends UserDetailsService {

    Session startNewSession(String oldSessionId);

    RestUserDetails retrieveRestUserDetails(String sessionId);

    void logout();
}

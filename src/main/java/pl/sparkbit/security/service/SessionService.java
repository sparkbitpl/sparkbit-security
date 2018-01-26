package pl.sparkbit.security.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import pl.sparkbit.security.domain.Session;

public interface SessionService extends UserDetailsService {

    Session startNewSession(String oldAuthToken);

    void endSession();
}

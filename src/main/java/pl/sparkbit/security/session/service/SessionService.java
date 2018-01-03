package pl.sparkbit.security.session.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import pl.sparkbit.security.session.domain.Session;

public interface SessionService extends UserDetailsService {

    Session startNewSession(String oldAuthToken);

    void logout();
}

package pl.sparkbit.security.service;

import pl.sparkbit.security.domain.RestUserDetails;

public interface UserDetailsService extends org.springframework.security.core.userdetails.UserDetailsService {

    RestUserDetails retrieveRestUserDetails(String authToken);
}

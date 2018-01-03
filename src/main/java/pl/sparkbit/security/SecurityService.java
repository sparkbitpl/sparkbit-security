package pl.sparkbit.security;

import pl.sparkbit.security.rest.user.RestUserDetails;

public interface SecurityService {

    RestUserDetails retrieveRestUserDetails(String authToken);
}

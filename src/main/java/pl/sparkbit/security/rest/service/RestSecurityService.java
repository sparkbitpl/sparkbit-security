package pl.sparkbit.security.rest.service;

import pl.sparkbit.security.rest.domain.RestUserDetails;

public interface RestSecurityService {

    RestUserDetails retrieveRestUserDetails(String authToken);
}

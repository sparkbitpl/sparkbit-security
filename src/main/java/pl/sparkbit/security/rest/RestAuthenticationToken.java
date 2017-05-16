package pl.sparkbit.security.rest;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class RestAuthenticationToken extends AbstractAuthenticationToken {

    private final RestUserDetails restUserDetails;

    RestAuthenticationToken(RestUserDetails restUserDetails) {
        super(restUserDetails.getAuthorities());
        this.restUserDetails = restUserDetails;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return restUserDetails;
    }
}

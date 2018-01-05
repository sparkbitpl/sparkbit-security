package pl.sparkbit.security.rest.authn.user;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import pl.sparkbit.security.rest.domain.RestUserDetails;

public class UserAuthenticationToken extends AbstractAuthenticationToken {

    private final RestUserDetails restUserDetails;

    UserAuthenticationToken(RestUserDetails restUserDetails) {
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

package pl.sparkbit.security.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;
import pl.sparkbit.security.SecurityService;

@RequiredArgsConstructor
public class RestAuthenticationProvider implements AuthenticationProvider {

    private final SecurityService securityService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(AuthTokenAuthenticationToken.class, authentication,
                "Only AuthTokenAuthenticationToken is supported");

        String authToken = ((AuthTokenAuthenticationToken) authentication).getAuthToken();
        RestUserDetails restUserDetails = securityService.retrieveRestUserDetails(authToken);

        Assert.notNull(restUserDetails, "Returned restUserDetails should never be null - method contract violation");

        return new RestAuthenticationToken(restUserDetails);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (AuthTokenAuthenticationToken.class.isAssignableFrom(authentication));
    }
}

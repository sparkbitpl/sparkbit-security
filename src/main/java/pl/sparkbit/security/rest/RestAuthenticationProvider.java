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
        Assert.isInstanceOf(SessionIdAuthenticationToken.class, authentication,
                "Only SessionIdAuthenticationToken is supported");

        String sessionId = ((SessionIdAuthenticationToken) authentication).getSessionId();
        RestUserDetails restUserDetails = securityService.retrieveRestUserDetails(sessionId);

        Assert.notNull(restUserDetails, "Returned restUserDetails should never be null - method contract violation");

        return new RestAuthenticationToken(restUserDetails);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (SessionIdAuthenticationToken.class.isAssignableFrom(authentication));
    }
}

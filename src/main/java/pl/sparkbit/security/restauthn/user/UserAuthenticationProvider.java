package pl.sparkbit.security.restauthn.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;
import pl.sparkbit.security.domain.RestUserDetails;
import pl.sparkbit.security.restauthn.RestAuthenticationToken;
import pl.sparkbit.security.service.RestSecurityService;

@RequiredArgsConstructor
public class UserAuthenticationProvider implements AuthenticationProvider {

    private final RestSecurityService restSecurityService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(RestAuthenticationToken.class, authentication, "Only RestAuthenticationToken is supported");

        String authToken = ((RestAuthenticationToken) authentication).getAuthToken();
        RestUserDetails restUserDetails = restSecurityService.retrieveRestUserDetails(authToken);

        Assert.notNull(restUserDetails, "Returned restUserDetails should never be null - method contract violation");

        return new UserAuthenticationToken(restUserDetails);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return RestAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

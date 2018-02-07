package pl.sparkbit.security.restauthn.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.util.Assert;
import pl.sparkbit.security.domain.RestUserDetails;
import pl.sparkbit.security.restauthn.RestAuthenticationToken;
import pl.sparkbit.security.service.SessionService;
import pl.sparkbit.security.service.UserDetailsService;

@RequiredArgsConstructor
public class UserAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final SessionService sessionService;
    private final UserDetailsChecker authenticationChecks = new AccountStatusUserDetailsChecker();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(RestAuthenticationToken.class, authentication, "Only RestAuthenticationToken is supported");

        String authToken = ((RestAuthenticationToken) authentication).getAuthToken();
        RestUserDetails restUserDetails = userDetailsService.retrieveRestUserDetails(authToken);

        Assert.notNull(restUserDetails, "Returned restUserDetails should never be null - method contract violation");
        authenticationChecks.check(restUserDetails);

        if (!sessionService.areSessionsImmortal()) {
            sessionService.updateSessionExpiryTs(authToken);
        }

        return new UserAuthenticationToken(restUserDetails);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return RestAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

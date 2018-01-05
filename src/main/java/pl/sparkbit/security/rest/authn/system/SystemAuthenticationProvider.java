package pl.sparkbit.security.rest.authn.system;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import pl.sparkbit.security.rest.authn.RestAuthenticationToken;

@RequiredArgsConstructor
@SuppressWarnings("unused")
public class SystemAuthenticationProvider implements AuthenticationProvider {

    private final String externalSystemName;
    private final String expectedAuthToken;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Object authToken = authentication.getPrincipal();
        if (expectedAuthToken.equals(authToken)) {
            return new SystemAuthenticationToken(externalSystemName);
        } else {
            throw new BadCredentialsException("Invalid token");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (RestAuthenticationToken.class.isAssignableFrom(authentication));
    }
}

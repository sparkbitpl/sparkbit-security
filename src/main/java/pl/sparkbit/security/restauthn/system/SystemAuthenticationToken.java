package pl.sparkbit.security.restauthn.system;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import static java.util.Collections.singleton;
import static pl.sparkbit.security.Security.SYSTEM_ROLE;

public class SystemAuthenticationToken extends AbstractAuthenticationToken {

    private final String externalSystemName;

    SystemAuthenticationToken(String externalSystemName) {
        super(singleton(SYSTEM_ROLE));
        this.externalSystemName = externalSystemName;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return externalSystemName;
    }
}

package pl.sparkbit.security.rest.authn.system;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import static java.util.Collections.singleton;
import static pl.sparkbit.security.Security.EXTERNAL_SYSTEM_ROLE;

public class SystemAuthenticationToken extends AbstractAuthenticationToken {

    private final String externalSystemName;

    SystemAuthenticationToken(String externalSystemName) {
        super(singleton(EXTERNAL_SYSTEM_ROLE));
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

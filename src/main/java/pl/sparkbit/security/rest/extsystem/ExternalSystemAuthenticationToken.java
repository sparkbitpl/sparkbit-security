package pl.sparkbit.security.rest.extsystem;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import static java.util.Collections.singleton;
import static pl.sparkbit.security.Security.EXTERNAL_SYSTEM_ROLE;

public class ExternalSystemAuthenticationToken extends AbstractAuthenticationToken {

    private final String externalSystemName;

    public ExternalSystemAuthenticationToken(String externalSystemName) {
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

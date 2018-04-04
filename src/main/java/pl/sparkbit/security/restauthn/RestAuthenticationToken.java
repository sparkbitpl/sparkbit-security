package pl.sparkbit.security.restauthn;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;

public class RestAuthenticationToken extends AbstractAuthenticationToken {

    @Getter
    private final String authToken;

    RestAuthenticationToken(String authToken) {
        super(Collections.emptyList());

        this.authToken = authToken;
        setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}

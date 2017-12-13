package pl.sparkbit.security.rest;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;

public class AuthTokenAuthenticationToken extends AbstractAuthenticationToken {

    @Getter
    private final String authToken;

    public AuthTokenAuthenticationToken(String authToken) {
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
        return authToken;
    }
}

package pl.sparkbit.security.rest;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;

public class SessionIdAuthenticationToken extends AbstractAuthenticationToken {

    @Getter
    private final String sessionId;

    SessionIdAuthenticationToken(String sessionId) {
        super(Collections.emptyList());

        this.sessionId = sessionId;
        setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return sessionId;
    }
}

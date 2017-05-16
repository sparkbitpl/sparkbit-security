package pl.sparkbit.security.rest;

import org.springframework.security.core.AuthenticationException;

public class SessionNotFoundException extends AuthenticationException {

    public SessionNotFoundException(String msg) {
        super(msg);
    }
}

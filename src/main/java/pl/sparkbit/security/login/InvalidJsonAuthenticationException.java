package pl.sparkbit.security.login;

import org.springframework.security.core.AuthenticationException;

public class InvalidJsonAuthenticationException extends AuthenticationException {

    InvalidJsonAuthenticationException(String msg) {
        super(msg);
    }

    InvalidJsonAuthenticationException(String msg, Throwable t) {
        super(msg, t);
    }
}

package pl.sparkbit.security.login;

import org.springframework.security.core.AuthenticationException;

class InvalidJsonException extends AuthenticationException {

    InvalidJsonException(String msg) {
        super(msg);
    }

    InvalidJsonException(String msg, Throwable t) {
        super(msg, t);
    }
}

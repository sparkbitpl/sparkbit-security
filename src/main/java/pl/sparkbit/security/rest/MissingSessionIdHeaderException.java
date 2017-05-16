package pl.sparkbit.security.rest;

import org.springframework.security.core.AuthenticationException;

class MissingSessionIdHeaderException extends AuthenticationException {

    MissingSessionIdHeaderException(String msg) {
        super(msg);
    }
}

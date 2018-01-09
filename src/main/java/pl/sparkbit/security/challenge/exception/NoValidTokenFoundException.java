package pl.sparkbit.security.challenge.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoValidTokenFoundException extends RuntimeException {

    @Getter
    private final FailureReason reason;

    public NoValidTokenFoundException(String msg, FailureReason reason) {
        super(msg);
        this.reason = reason;
    }

    public enum FailureReason {
        TOKEN_NOT_FOUND, TOKEN_EXPIRED
    }
}

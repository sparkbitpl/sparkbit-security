package pl.sparkbit.security.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.sparkbit.commons.exception.BusinessException;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidCurrentPasswordException extends BusinessException {

    public InvalidCurrentPasswordException(String message) {
        super(message);
    }
}

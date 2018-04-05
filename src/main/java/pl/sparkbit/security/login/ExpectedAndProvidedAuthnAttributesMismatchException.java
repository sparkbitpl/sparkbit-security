package pl.sparkbit.security.login;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

import java.util.Set;

@Getter
public class ExpectedAndProvidedAuthnAttributesMismatchException extends AuthenticationException {

    private final Set<String> expected;
    private final Set<String> provided;

    ExpectedAndProvidedAuthnAttributesMismatchException(Set<String> expected, Set<String> provided) {
        super("Mismatched authnAttributes. Expected: " + expected + ", provided: " + provided);
        this.expected = expected;
        this.provided = provided;
    }
}

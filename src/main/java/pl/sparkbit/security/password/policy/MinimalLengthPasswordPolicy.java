package pl.sparkbit.security.password.policy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "sparkbit.security.defaultPasswordPolicy.enabled", havingValue = "true",
        matchIfMissing = true)
@SuppressWarnings("unused")
public class MinimalLengthPasswordPolicy implements PasswordPolicy {

    @Value("${sparkbit.security.minPasswordLength:8}")
    private int minimalLength;

    @Override
    public boolean isValid(String password) {
        return password.length() >= minimalLength;
    }
}

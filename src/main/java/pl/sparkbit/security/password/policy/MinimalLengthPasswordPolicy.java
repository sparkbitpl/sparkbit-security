package pl.sparkbit.security.password.policy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static pl.sparkbit.security.Properties.DEFAULT_PASSWORD_POLICY_ENABLED;
import static pl.sparkbit.security.Properties.MINIMAL_PASSWORD_LENGTH;

@Component
@ConditionalOnProperty(value = DEFAULT_PASSWORD_POLICY_ENABLED, havingValue = "true", matchIfMissing = true)
@SuppressWarnings("unused")
public class MinimalLengthPasswordPolicy implements PasswordPolicy {

    @Value("${" + MINIMAL_PASSWORD_LENGTH + ":8}")
    private int minimalLength;

    @Override
    public boolean isValid(String password) {
        return password.length() >= minimalLength;
    }
}

package pl.sparkbit.security.password.policy;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import pl.sparkbit.security.config.Properties;

import static pl.sparkbit.security.config.Properties.DEFAULT_PASSWORD_POLICY_ENABLED;

@Component
@ConditionalOnProperty(value = DEFAULT_PASSWORD_POLICY_ENABLED, havingValue = "true")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class MinimalLengthPasswordPolicy implements PasswordPolicy {

    private final Properties configuration;

    @Override
    public boolean isValid(String password) {
        return password.length() >= configuration.getMinPasswordLength();
    }
}

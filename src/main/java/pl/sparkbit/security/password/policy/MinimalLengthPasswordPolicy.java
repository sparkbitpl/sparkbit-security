package pl.sparkbit.security.password.policy;

import lombok.RequiredArgsConstructor;
import pl.sparkbit.security.config.SecurityProperties;

@RequiredArgsConstructor
public class MinimalLengthPasswordPolicy implements PasswordPolicy {

    private final SecurityProperties configuration;

    @Override
    public boolean isValid(String password) {
        return password.length() >= configuration.getDefaultPasswordPolicy().getMinPasswordLength();
    }
}

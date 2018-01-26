package pl.sparkbit.security.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.sparkbit.commons.util.RandomStringGenerator;

import static pl.sparkbit.security.config.Properties.CHALLENGE_TOKEN_ALLOWED_CHARACTERS;
import static pl.sparkbit.security.config.Properties.CHALLENGE_TOKEN_LENGTH;

@Component
@SuppressWarnings("unused")
public class SecurityChallengeTokenGeneratorImpl implements SecurityChallengeTokenGenerator {

    @Value("${" + CHALLENGE_TOKEN_LENGTH + ":6}")
    private int tokenLength;

    @Value("${" + CHALLENGE_TOKEN_ALLOWED_CHARACTERS + ":23456789ABCDEFGHIJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz}")
    private String allowedCharacters;

    @Override
    public String generateChallengeToken() {
        return RandomStringGenerator.randomString(tokenLength, allowedCharacters);
    }
}

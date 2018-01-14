package pl.sparkbit.security.challenge.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.sparkbit.commons.util.RandomStringGenerator;

@Component
@SuppressWarnings("unused")
public class SecurityChallengeTokenGeneratorImpl implements SecurityChallengeTokenGenerator {

    @Value("${sparkbit.security.challengeToken.length:6}")
    private int tokenLength;

    @Value("${sparkbit.security.challengeToken.allowedCharacters:" +
            "23456789ABCDEFGHIJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz}")
    private String allowedCharacters;

    @Override
    public String generateChallengeToken() {
        return RandomStringGenerator.randomString(tokenLength, allowedCharacters);
    }
}

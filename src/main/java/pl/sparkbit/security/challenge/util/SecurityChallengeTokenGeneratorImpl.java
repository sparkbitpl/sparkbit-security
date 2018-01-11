package pl.sparkbit.security.challenge.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@SuppressWarnings("unused")
public class SecurityChallengeTokenGeneratorImpl implements SecurityChallengeTokenGenerator {

    private static final SecureRandom RND = new SecureRandom();

    @Value("${sparkbit.security.challengeToken.length:6}")
    private int tokenLength;

    @Value("${sparkbit.security.challengeToken.allowedCharacters:" +
            "23456789ABCDEFGHIJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz}")
    private String allowedCharacters;

    @Override
    public String generateChallengeToken() {
        return randomString(tokenLength);
    }

    private String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(allowedCharacters.charAt(RND.nextInt(allowedCharacters.length())));
        }
        return sb.toString();
    }
}

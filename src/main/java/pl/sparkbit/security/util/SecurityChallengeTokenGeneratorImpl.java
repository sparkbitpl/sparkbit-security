package pl.sparkbit.security.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static pl.sparkbit.security.config.Properties.CHALLENGE_TOKEN_ALLOWED_CHARACTERS;
import static pl.sparkbit.security.config.Properties.CHALLENGE_TOKEN_LENGTH;
import static pl.sparkbit.security.util.SecureRandomStringGeneratorImpl.BASE_58_CHARACTERS;

@Component
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class SecurityChallengeTokenGeneratorImpl implements SecurityChallengeTokenGenerator {

    private final SecureRandomStringGenerator secureRandomStringGenerator;

    @Value("${" + CHALLENGE_TOKEN_LENGTH + ":6}")
    private int tokenLength;

    @Value("${" + CHALLENGE_TOKEN_ALLOWED_CHARACTERS + ":" + BASE_58_CHARACTERS + "}")
    private String allowedCharacters;

    @Override
    public String generateChallengeToken() {
        return secureRandomStringGenerator.randomString(tokenLength, allowedCharacters);
    }
}

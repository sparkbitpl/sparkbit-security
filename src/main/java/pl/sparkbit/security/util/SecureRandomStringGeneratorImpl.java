package pl.sparkbit.security.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@SuppressWarnings({"unused", "WeakerAccess", "checkstyle:hideutilityclassconstructor"})
public class SecureRandomStringGeneratorImpl implements SecureRandomStringGenerator {

    public static final String DIGITS = "01234567890";
    public static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    public static final String BASE_58_CHARACTERS = "23456789ABCDEFGHIJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";

    private static final SecureRandom RND = new SecureRandom();

    @Override
    public String randomString(int len, String allowedCharacters) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(allowedCharacters.charAt(RND.nextInt(allowedCharacters.length())));
        }
        return sb.toString();
    }

    @Override
    public String digitsString(int len) {
        return randomString(len, DIGITS);
    }

    @Override
    public String uppercaseLetterString(int len) {
        return randomString(len, UPPERCASE_LETTERS);
    }

    @Override
    public String lowercaseLetterString(int len) {
        return randomString(len, LOWERCASE_LETTERS);
    }

    @Override
    public String base58String(int len) {
        return randomString(len, BASE_58_CHARACTERS);
    }
}

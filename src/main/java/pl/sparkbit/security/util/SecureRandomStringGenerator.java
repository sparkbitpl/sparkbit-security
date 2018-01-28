package pl.sparkbit.security.util;

@SuppressWarnings({"unused", "WeakerAccess"})
public interface SecureRandomStringGenerator {

    String randomString(int len, String allowedCharacters);

    String digitsString(int len);

    String uppercaseLetterString(int len);

    String lowercaseLetterString(int len);

    String base58String(int len);
}

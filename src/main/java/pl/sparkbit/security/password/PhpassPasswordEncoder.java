package pl.sparkbit.security.password;

import org.springframework.security.crypto.password.PasswordEncoder;

public class PhpassPasswordEncoder implements PasswordEncoder {

    private static final int NUMBER_OF_HASH_ITERATIONS = 8;

    private PHPass phpass;

    public PhpassPasswordEncoder() {
        this.phpass = new PHPass(NUMBER_OF_HASH_ITERATIONS);
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return phpass.hashPassword(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return phpass.checkPassword(rawPassword.toString(), encodedPassword);
    }
}

package pl.sparkbit.security.password.encoder;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class AuthTokenHasherImpl implements AuthTokenHasher {

    @Override
    public String hash(String token) {
        return Hashing
                .sha256()
                .hashString(token, StandardCharsets.UTF_8)
                .toString();
    }
}

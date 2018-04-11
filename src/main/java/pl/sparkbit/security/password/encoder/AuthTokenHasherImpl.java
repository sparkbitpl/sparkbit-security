package pl.sparkbit.security.password.encoder;

import com.google.common.hash.Hashing;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class AuthTokenHasherImpl implements AuthTokenHasher {

    public String hash(String token) {
        return Hashing
                .sha256()
                .hashString(token, StandardCharsets.UTF_8)
                .toString();
    }
}

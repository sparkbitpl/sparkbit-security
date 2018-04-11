package pl.sparkbit.security.password.encoder;

public interface AuthTokenHasher {

    String hash(String token);

}

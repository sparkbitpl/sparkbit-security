package pl.sparkbit.security.login;

import org.springframework.security.core.Authentication;

import java.util.Set;

public interface AuthenticationTokenHolder {

    Authentication toToken(Set<String> expectedAuthnAttributes);
}

package pl.sparkbit.security.login;

import org.springframework.security.core.Authentication;

public interface AuthenticationTokenHolder {

    Authentication toToken(LoginPrincipal loginPrincipal);
}

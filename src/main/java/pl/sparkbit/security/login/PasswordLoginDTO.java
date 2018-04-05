package pl.sparkbit.security.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@SuppressWarnings("unused")
public class PasswordLoginDTO extends LoginDTO {

    @JsonProperty
    private String password;

    @Override
    public Authentication toToken(LoginPrincipal loginPrincipal) {
        return new UsernamePasswordAuthenticationToken(loginPrincipal, password);
    }
}

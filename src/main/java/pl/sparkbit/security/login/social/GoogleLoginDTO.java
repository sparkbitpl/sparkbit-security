package pl.sparkbit.security.login.social;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.Authentication;
import pl.sparkbit.security.login.LoginDTO;
import pl.sparkbit.security.login.LoginPrincipal;

@SuppressWarnings("unused")
public class GoogleLoginDTO extends LoginDTO {

    @JsonProperty
    private String googleIdToken;

    @Override
    public Authentication toToken(LoginPrincipal loginPrincipal) {
        return new GoogleAuthenticationToken(googleIdToken, loginPrincipal);
    }
}

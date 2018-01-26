package pl.sparkbit.security.login.social;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import pl.sparkbit.security.login.AuthnAttributes;
import pl.sparkbit.security.login.LoginDTO;
import pl.sparkbit.security.login.LoginPrincipal;

import java.util.Set;

@Getter
@SuppressWarnings("unused")
public class GoogleLoginDTO extends LoginDTO {

    @JsonProperty
    private String googleIdToken;

    public GoogleLoginDTO() {
        super(null);
    }

    @Override
    public Authentication toToken(Set<String> expectedAuthnAttributes) {
        AuthnAttributes authnAttributes =
                new AuthnAttributes(getAuthnAttributes(), expectedAuthnAttributes);
        LoginPrincipal principal = new LoginPrincipal(authnAttributes);
        return new GoogleAuthenticationToken(googleIdToken, principal);
    }
}

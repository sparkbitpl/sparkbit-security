package pl.sparkbit.security.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Set;

@Getter
@SuppressWarnings("unused")
public class PasswordLoginDTO extends LoginDTO {

    @JsonProperty
    private String password;

    public PasswordLoginDTO() {
        super(null);
    }

    @Override
    public Authentication toToken(Set<String> expectedAuthnAttributes) {
        AuthnAttributes authnAttributes = new AuthnAttributes(getAuthnAttributes(), expectedAuthnAttributes);
        LoginPrincipal principal = new LoginPrincipal(authnAttributes);
        return new UsernamePasswordAuthenticationToken(principal, getPassword());
    }
}

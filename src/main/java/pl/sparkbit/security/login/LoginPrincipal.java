package pl.sparkbit.security.login;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.security.Principal;

@Getter
@RequiredArgsConstructor
@ToString
public class LoginPrincipal implements Principal {

    private final AuthnAttributes authnAttributes;

    public LoginPrincipal(String principalData) {
        authnAttributes = new AuthnAttributes(principalData);
    }

    @Override
    public String getName() {
        return authnAttributes.toString();
    }
}

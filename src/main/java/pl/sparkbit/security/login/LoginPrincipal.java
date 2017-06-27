package pl.sparkbit.security.login;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.security.Principal;

@Getter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class LoginPrincipal implements Principal, Serializable {

    private final AuthnAttributes authnAttributes;

    public LoginPrincipal(String principalData) {
        authnAttributes = new AuthnAttributes(principalData);
    }

    @Override
    public String getName() {
        return authnAttributes.toString();
    }
}

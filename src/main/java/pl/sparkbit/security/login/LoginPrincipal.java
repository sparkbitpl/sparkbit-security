package pl.sparkbit.security.login;

import lombok.*;

import java.io.Serializable;
import java.security.Principal;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@ToString
public class LoginPrincipal implements Principal, Serializable {

    private final AuthnAttributes authnAttributes;

    LoginPrincipal(String principalData) {
        authnAttributes = new AuthnAttributes(principalData);
    }

    @Override
    public String getName() {
        return authnAttributes.toString();
    }
}

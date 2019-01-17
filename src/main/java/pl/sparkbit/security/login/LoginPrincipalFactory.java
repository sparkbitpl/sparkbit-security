package pl.sparkbit.security.login;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class LoginPrincipalFactory {

    private final Set<String> expectedAttributes;

    public LoginPrincipal generate(Map<String, String> providedAttributes) {
        validateAuthnAttributes(providedAttributes);
        AuthnAttributes authnAttributes = new AuthnAttributes(providedAttributes);
        return new LoginPrincipal(authnAttributes);
    }

    public LoginPrincipal generate(String encodedAttributes) {
        LoginPrincipal loginPrincipal = new LoginPrincipal(encodedAttributes);
        validateAuthnAttributes(loginPrincipal.getAuthnAttributes());
        return loginPrincipal;
    }

    private void validateAuthnAttributes(Map<String, String> providedAttributes) {
        if (providedAttributes == null) {
            throw new ExpectedAndProvidedAuthnAttributesMismatchException(expectedAttributes, null);
        }
        if (!providedAttributes.keySet().equals(expectedAttributes)) {
            throw new ExpectedAndProvidedAuthnAttributesMismatchException(expectedAttributes,
                    providedAttributes.keySet());
        }
    }
}

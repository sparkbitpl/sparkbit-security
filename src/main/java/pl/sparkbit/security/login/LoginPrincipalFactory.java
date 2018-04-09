package pl.sparkbit.security.login;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static pl.sparkbit.security.config.Properties.EXPECTED_AUTHN_ATTRIBUTES;

@Component
@Slf4j
public class LoginPrincipalFactory {

    private final Set<String> expectedAttributes;

    public LoginPrincipalFactory(@Value("${" + EXPECTED_AUTHN_ATTRIBUTES + "}") String[] expectedAuthnAttributes) {
        this.expectedAttributes = Arrays.stream(expectedAuthnAttributes).collect(toSet());
    }

    public LoginPrincipal generate(Map<String, String> providedAttributes) {
        validateAuthnAttributes(providedAttributes);
        AuthnAttributes authnAttributes = new AuthnAttributes(providedAttributes);
        return new LoginPrincipal(authnAttributes);
    }

    public LoginPrincipal generate(String encodedAttributes) {
        LoginPrincipal loginPrincipal = new LoginPrincipal(encodedAttributes);
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

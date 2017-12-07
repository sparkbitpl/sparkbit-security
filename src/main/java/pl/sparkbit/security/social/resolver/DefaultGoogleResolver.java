package pl.sparkbit.security.social.resolver;

import lombok.RequiredArgsConstructor;
import pl.sparkbit.security.login.AuthnAttributes;

import java.util.List;

@RequiredArgsConstructor
public class DefaultGoogleResolver implements GoogleResolver {

    private final List<String> googleClientIds;

    @Override
    public GoogleSecrets resolve(AuthnAttributes authn) {
        return GoogleSecrets.builder()
                .googleClientIds(googleClientIds)
                .build();
    }
}

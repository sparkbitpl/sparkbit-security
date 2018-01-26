package pl.sparkbit.security.session.auth.social.resolver;

import lombok.RequiredArgsConstructor;
import pl.sparkbit.security.session.auth.AuthnAttributes;

import java.util.List;

@RequiredArgsConstructor
@SuppressWarnings("unused")
public class DefaultGoogleResolver implements GoogleResolver {

    private final List<String> googleClientIds;

    @Override
    public GoogleSecrets resolve(AuthnAttributes authn) {
        return GoogleSecrets.builder()
                .googleClientIds(googleClientIds)
                .build();
    }
}

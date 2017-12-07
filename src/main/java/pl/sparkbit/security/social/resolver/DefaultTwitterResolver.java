package pl.sparkbit.security.social.resolver;

import lombok.RequiredArgsConstructor;
import pl.sparkbit.security.login.AuthnAttributes;

@RequiredArgsConstructor
public class DefaultTwitterResolver implements TwitterResolver {

    private final String appKey;
    private final String appSecret;
    private final String verifyUrl;

    @Override
    public TwitterSecrets resolve(AuthnAttributes authn) {
        return TwitterSecrets.builder()
                .appKey(appKey)
                .appSecret(appSecret)
                .verifyUrl(verifyUrl)
                .build();
    }
}

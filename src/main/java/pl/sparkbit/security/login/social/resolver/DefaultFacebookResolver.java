package pl.sparkbit.security.login.social.resolver;

import lombok.RequiredArgsConstructor;
import pl.sparkbit.security.login.AuthnAttributes;

@RequiredArgsConstructor
public class DefaultFacebookResolver implements FacebookResolver {

    private final String appKey;
    private final String appSecret;
    private final String redirectUri;
    private final String verifyUrl;

    @Override
    public FacebookSecrets resolve(AuthnAttributes authn) {
        return FacebookSecrets.builder()
                .appKey(appKey)
                .appSecret(appSecret)
                .redirectUri(redirectUri)
                .verifyUrl(verifyUrl)
                .build();
    }
}

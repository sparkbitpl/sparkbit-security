package pl.sparkbit.security.login.social.resolver;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FacebookSecrets {

    private final String appKey;
    private final String appSecret;
    private final String redirectUri;
    private final String verifyUrl;

}

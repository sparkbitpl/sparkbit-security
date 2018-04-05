package pl.sparkbit.security.login.social;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.Authentication;
import pl.sparkbit.security.domain.TwitterCredentials;
import pl.sparkbit.security.login.LoginDTO;
import pl.sparkbit.security.login.LoginPrincipal;

@SuppressWarnings("unused")
public class TwitterLoginDTO extends LoginDTO {

    @JsonProperty
    private String oauthToken;
    @JsonProperty
    private String oauthTokenSecret;

    @Override
    public Authentication toToken(LoginPrincipal loginPrincipal) {
        TwitterCredentials credentials = TwitterCredentials.builder()
                .oauthToken(oauthToken)
                .oauthTokenSecret(oauthTokenSecret)
                .build();
        return new TwitterAuthenticationToken(credentials, loginPrincipal);
    }
}

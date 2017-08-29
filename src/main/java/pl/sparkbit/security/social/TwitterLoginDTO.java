package pl.sparkbit.security.social;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import pl.sparkbit.security.domain.TwitterCredentials;
import pl.sparkbit.security.login.AuthnAttributes;
import pl.sparkbit.security.login.LoginDTO;
import pl.sparkbit.security.login.LoginPrincipal;

import java.util.Set;

@Getter
@SuppressWarnings("unused")
public class TwitterLoginDTO extends LoginDTO {

    @JsonProperty
    private String oauthToken;
    @JsonProperty
    private String oauthTokenSecret;

    public TwitterLoginDTO() {
        super(null);
    }

    @Override
    public Authentication toToken(Set<String> expectedAuthnAttributes) {
        AuthnAttributes authnAttributes =
                new AuthnAttributes(getAuthnAttributes(), expectedAuthnAttributes);
        LoginPrincipal principal = new LoginPrincipal(authnAttributes);
        TwitterCredentials credentials = TwitterCredentials.builder()
                .oauthToken(oauthToken)
                .oauthTokenSecret(oauthTokenSecret)
                .build();
        return new TwitterAuthenticationToken(credentials, principal);
    }
}

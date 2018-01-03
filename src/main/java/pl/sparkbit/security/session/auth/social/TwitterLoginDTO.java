package pl.sparkbit.security.session.auth.social;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import pl.sparkbit.security.session.auth.AuthnAttributes;
import pl.sparkbit.security.session.auth.LoginDTO;
import pl.sparkbit.security.session.auth.LoginPrincipal;
import pl.sparkbit.security.session.domain.TwitterCredentials;

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

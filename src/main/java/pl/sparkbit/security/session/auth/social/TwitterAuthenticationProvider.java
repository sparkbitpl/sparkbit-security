package pl.sparkbit.security.session.auth.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import lombok.Data;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;
import pl.sparkbit.security.session.auth.AuthnAttributes;
import pl.sparkbit.security.session.auth.LoginPrincipal;
import pl.sparkbit.security.session.auth.social.resolver.TwitterResolver;
import pl.sparkbit.security.session.auth.social.resolver.TwitterSecrets;
import pl.sparkbit.security.session.domain.TwitterCredentials;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class TwitterAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;
    private final UserDetailsChecker authenticationChecks = new AccountStatusUserDetailsChecker();
    private final TwitterResolver resolver;

    public TwitterAuthenticationProvider(TwitterResolver resolver, UserDetailsService userDetailsService,
            ObjectMapper objectMapper) {
        this.resolver = resolver;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(TwitterAuthenticationToken.class, authentication,
                "Only TwitterAuthenticationToken supported");
        TwitterAuthenticationToken token = (TwitterAuthenticationToken) authentication;
        UserDetails user;
        try {
            user = verify(token);
        } catch (UsernameNotFoundException ex) {
            throw new BadCredentialsException("Bad credentials");
        }

        Assert.notNull(user,
                "verify returned null - a violation of the interface contract");
        authenticationChecks.check(user);
        return new TwitterAuthenticationToken((TwitterCredentials) token.getCredentials(), user, user.getAuthorities());
    }

    @SuppressWarnings("checkstyle:IllegalCatch")
    private UserDetails verify(TwitterAuthenticationToken authentication) throws AuthenticationException {
        try {
            Assert.isInstanceOf(TwitterCredentials.class, authentication.getCredentials(),
                    "Illegal credentials");
            TwitterCredentials credentials = (TwitterCredentials) authentication.getCredentials();
            OAuth1AccessToken accessToken = new OAuth1AccessToken(credentials.getOauthToken(),
                    credentials.getOauthTokenSecret());

            AuthnAttributes authn = ((LoginPrincipal) authentication.getPrincipal()).getAuthnAttributes();
            TwitterSecrets secrets = resolver.resolve(authn);
            String appKey = secrets.getAppKey();
            String appSecret = secrets.getAppSecret();
            String verifyUrl = secrets.getVerifyUrl();

            OAuthRequest request = new OAuthRequest(Verb.GET, verifyUrl);
            OAuth10aService service = new ServiceBuilder(appKey)
                    .apiSecret(appSecret)
                    .build(TwitterApi.instance());
            service.signRequest(accessToken, request);
            Response response = service.execute(request);
            if (!response.isSuccessful()) {
                throw new BadCredentialsException("Twitter Token is invalid");
            }

            ResultObject result = objectMapper.readValue(response.getBody(), ResultObject.class);

            LoginPrincipal principal = (LoginPrincipal) authentication.getPrincipal();
            String email = principal.getAuthnAttributes().get("email");
            if (email == null) {
                throw new BadCredentialsException("No email given");
            }

            if (!email.equals(result.getEmail())) {
                throw new BadCredentialsException("Email from Twitter servers does not match the given email");
            }
            return userDetailsService.loadUserByUsername(authentication.getName());
        } catch (IOException | InterruptedException | ExecutionException | RuntimeException e) {
            throw new BadCredentialsException("Twitter Token is invalid", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (TwitterAuthenticationToken.class.isAssignableFrom(authentication));
    }

    @Data
    private static class ResultObject {
        private String email;
    }
}

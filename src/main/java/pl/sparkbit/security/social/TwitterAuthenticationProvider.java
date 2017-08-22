package pl.sparkbit.security.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
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
import pl.sparkbit.security.domain.TwitterCredentials;
import pl.sparkbit.security.login.LoginPrincipal;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class TwitterAuthenticationProvider implements AuthenticationProvider {

    private final String verifyUrl;
    private final OAuth10aService service;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;
    private final UserDetailsChecker authenticationChecks = new AccountStatusUserDetailsChecker();

    public TwitterAuthenticationProvider(String appKey, String appSecret, String verifyUrl,
                                         UserDetailsService userDetailsService, ObjectMapper objectMapper) {
        this.verifyUrl = verifyUrl;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
        this.service = new ServiceBuilder(appKey)
                .apiSecret(appSecret)
                .build(TwitterApi.instance());
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

    private UserDetails verify(TwitterAuthenticationToken authentication) throws AuthenticationException {
        try {
            Assert.isInstanceOf(TwitterCredentials.class, authentication.getCredentials(),
                    "Illegal credentials");
            TwitterCredentials credentials = (TwitterCredentials) authentication.getCredentials();
            OAuth1RequestToken requestToken = new OAuth1RequestToken(credentials.getOauthToken(),
                    credentials.getOauthTokenSecret());
            OAuth1AccessToken accessToken = service.getAccessToken(requestToken, credentials.getOauthVerifier());
            OAuthRequest request = new OAuthRequest(Verb.GET, verifyUrl);
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
        } catch (IOException | IllegalArgumentException | InterruptedException | ExecutionException e) {
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

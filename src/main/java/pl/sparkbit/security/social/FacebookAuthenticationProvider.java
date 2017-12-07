package pl.sparkbit.security.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
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
import pl.sparkbit.security.login.AuthnAttributes;
import pl.sparkbit.security.login.LoginPrincipal;
import pl.sparkbit.security.social.resolver.FacebookResolver;
import pl.sparkbit.security.social.resolver.FacebookSecrets;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class FacebookAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;
    private final UserDetailsChecker authenticationChecks = new AccountStatusUserDetailsChecker();
    private final FacebookResolver resolver;

    public FacebookAuthenticationProvider(FacebookResolver resolver, UserDetailsService userDetailsService,
                                          ObjectMapper objectMapper) {
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
        this.resolver = resolver;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(FacebookAuthenticationToken.class, authentication,
                "Only FacebookAuthenticationToken supported");
        FacebookAuthenticationToken token = (FacebookAuthenticationToken) authentication;
        UserDetails user;
        try {
            user = verify(token);
        } catch (UsernameNotFoundException ex) {
            throw new BadCredentialsException("Bad credentials");
        }

        Assert.notNull(user,
                "verify returned null - a violation of the interface contract");
        authenticationChecks.check(user);
        return new FacebookAuthenticationToken(token.getCode(), token.getAccessToken(), user, user.getAuthorities());
    }

    @SuppressWarnings("checkstyle:IllegalCatch")
    private UserDetails verify(FacebookAuthenticationToken authentication) throws AuthenticationException {
        try {
            AuthnAttributes authn = ((LoginPrincipal) authentication.getPrincipal()).getAuthnAttributes();
            FacebookSecrets secrets = resolver.resolve(authn);
            OAuth20Service service = new ServiceBuilder(secrets.getAppKey())
                    .apiSecret(secrets.getAppSecret())
                    .callback(secrets.getRedirectUri())
                    .build(FacebookApi.instance());

            OAuth2AccessToken accessToken = getAccessToken(authentication, service);
            OAuthRequest request = new OAuthRequest(Verb.GET, secrets.getVerifyUrl());
            service.signRequest(accessToken, request);
            Response response = service.execute(request);
            if (!response.isSuccessful()) {
                throw new BadCredentialsException("Facebook code is invalid");
            }

            ResultObject result = objectMapper.readValue(response.getBody(), ResultObject.class);

            LoginPrincipal principal = (LoginPrincipal) authentication.getPrincipal();
            String email = principal.getAuthnAttributes().get("email");
            if (email == null) {
                throw new BadCredentialsException("No email given");
            }

            if (!email.equals(result.getEmail())) {
                throw new BadCredentialsException("Email from Facebook servers does not match the given email");
            }
            return userDetailsService.loadUserByUsername(authentication.getName());
        } catch (IOException | InterruptedException | ExecutionException | RuntimeException e) {
            throw new BadCredentialsException("Facebook code is invalid", e);
        }
    }

    private OAuth2AccessToken getAccessToken(FacebookAuthenticationToken authentication, OAuth20Service service)
            throws IOException, InterruptedException, ExecutionException {
        if (authentication.getAccessToken() != null) {
            return new OAuth2AccessToken(authentication.getAccessToken());
        }

        return service.getAccessToken(authentication.getCode());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (FacebookAuthenticationToken.class.isAssignableFrom(authentication));
    }

    @Data
    private static class ResultObject {
        private String email;
    }

}

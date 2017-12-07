package pl.sparkbit.security.social;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.extern.slf4j.Slf4j;
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
import pl.sparkbit.security.social.resolver.GoogleResolver;
import pl.sparkbit.security.social.resolver.GoogleSecrets;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Slf4j
public class GoogleAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final UserDetailsChecker authenticationChecks = new AccountStatusUserDetailsChecker();
    private final GoogleResolver resolver;
    private final HttpTransport transport;
    private final JsonFactory jsonFactory;

    public GoogleAuthenticationProvider(GoogleResolver resolver, UserDetailsService userDetailsService)
            throws GeneralSecurityException, IOException {
        this.transport = GoogleNetHttpTransport.newTrustedTransport();
        this.jsonFactory = JacksonFactory.getDefaultInstance();
        this.resolver = resolver;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        Assert.isInstanceOf(GoogleAuthenticationToken.class, authentication,
                "Only GoogleAuthenticationToken supported");

        GoogleAuthenticationToken token = (GoogleAuthenticationToken) authentication;
        UserDetails user;
        try {
            user = verify(token);
        } catch (UsernameNotFoundException ex) {
            throw new BadCredentialsException("Bad credentials");
        }

        Assert.notNull(user,
                "verify returned null - a violation of the interface contract");
        authenticationChecks.check(user);
        return new GoogleAuthenticationToken(token.getIdToken(), user,
                user.getAuthorities());
    }

    private UserDetails verify(GoogleAuthenticationToken authentication) throws AuthenticationException {
        try {
            AuthnAttributes authn = ((LoginPrincipal) authentication.getPrincipal()).getAuthnAttributes();
            GoogleSecrets secrets = resolver.resolve(authn);

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                    .setAudience(secrets.getGoogleClientIds())
                    .build();
            GoogleIdToken token = verifier.verify((String) authentication.getCredentials());
            if (token == null) {
                throw new BadCredentialsException("Google Id Token is invalid");
            }
            LoginPrincipal principal = (LoginPrincipal) authentication.getPrincipal();
            String email = principal.getAuthnAttributes().get("email");
            if (email == null) {
                throw new BadCredentialsException("No email given");
            }
            if (!email.equals(token.getPayload().getEmail())) {
                throw new BadCredentialsException("ID token does not match the given email");
            }
            return userDetailsService.loadUserByUsername(authentication.getName());
        } catch (GeneralSecurityException | IOException | IllegalArgumentException e) {
            throw new BadCredentialsException("Google Id Token is invalid", e);
        }
    }

    public boolean supports(Class<?> authentication) {
        return (GoogleAuthenticationToken.class.isAssignableFrom(authentication));
    }
}

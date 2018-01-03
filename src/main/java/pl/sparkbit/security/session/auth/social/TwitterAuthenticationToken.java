package pl.sparkbit.security.session.auth.social;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import pl.sparkbit.security.session.domain.TwitterCredentials;

import java.util.Collection;
import java.util.Collections;

public class TwitterAuthenticationToken extends AbstractAuthenticationToken {

    private TwitterCredentials credentials;
    private Object principal;

    public TwitterAuthenticationToken(TwitterCredentials credentials, Object principal,
                                      Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.credentials = credentials;
        this.principal = principal;
        setAuthenticated(true);
    }

    public TwitterAuthenticationToken(TwitterCredentials credentials, Object principal) {
        this(credentials, principal, Collections.emptyList());
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        credentials = null;
    }
}

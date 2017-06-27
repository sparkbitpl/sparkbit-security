package pl.sparkbit.security.social;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class GoogleAuthenticationToken extends AbstractAuthenticationToken {

    @Getter
    private String idToken;
    private Object principal;

    public GoogleAuthenticationToken(String idToken, Object principal,
            Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.idToken = idToken;
        this.principal = principal;
        super.setAuthenticated(true);
    }

    public GoogleAuthenticationToken(String idToken, Object principal) {
        this(idToken, principal, Collections.emptyList());
    }

    @Override
    public Object getCredentials() {
        return idToken;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        idToken = null;
    }
}

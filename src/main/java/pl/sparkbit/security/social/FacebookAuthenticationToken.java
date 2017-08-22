package pl.sparkbit.security.social;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

@Getter
public class FacebookAuthenticationToken extends AbstractAuthenticationToken {

    private String code;
    private Object principal;

    public FacebookAuthenticationToken(String code, Object principal,
                                       Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.code = code;
        this.principal = principal;
        setAuthenticated(true);
    }

    public FacebookAuthenticationToken(String code, Object principal) {
        this(code, principal, Collections.emptyList());
    }

    @Override
    public Object getCredentials() {
        return code;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        code = null;
    }

}

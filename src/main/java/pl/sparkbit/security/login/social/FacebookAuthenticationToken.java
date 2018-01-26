package pl.sparkbit.security.login.social;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

@Getter
public class FacebookAuthenticationToken extends AbstractAuthenticationToken {

    private String code;
    private String accessToken;
    private Object principal;

    public FacebookAuthenticationToken(String code, String accessToken, Object principal,
                                       Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.code = code;
        this.principal = principal;
        this.accessToken = accessToken;
        setAuthenticated(true);
    }

    public FacebookAuthenticationToken(String code, String accessToken, Object principal) {
        this(code, accessToken, principal, Collections.emptyList());
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

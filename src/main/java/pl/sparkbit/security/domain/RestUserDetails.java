package pl.sparkbit.security.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;

@Getter
@RequiredArgsConstructor
public class RestUserDetails implements UserDetails {

    private final String authToken;
    private final String userId;
    private final Instant expirationTimestamp;
    private final boolean extraAuthnCheckRequired;
    @SuppressWarnings("unused")
    private Collection<GrantedAuthority> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return authToken;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return expirationTimestamp == null || expirationTimestamp.isAfter(Instant.now());
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

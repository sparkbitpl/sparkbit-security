package pl.sparkbit.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.sparkbit.security.domain.RestUserDetails;

@Component
@SuppressWarnings({"unused", "WeakerAccess"})
public class Security {

    public static final String DEFAULT_AUTH_TOKEN_HEADER_NAME = "X-Sparkbit-Auth-Token";
    public static final String DEFAULT_SESSION_EXPIRATION_TIMESTAMP_HEADER_NAME =
            "X-Sparkbit-Session-Expiration-Timestamp";
    public static final String DEFAULT_AUTH_TOKEN_COOKIE_NAME = "sparkbitAuthToken";

    public static final String USER_ROLE_NAME = "ROLE_USER";
    public static final String ADMIN_ROLE_NAME = "ROLE_ADMIN";
    public static final String SYSTEM_ROLE_NAME = "ROLE_SYSTEM";
    public static final GrantedAuthority USER_ROLE = new SimpleGrantedAuthority(USER_ROLE_NAME);
    public static final GrantedAuthority ADMIN_ROLE = new SimpleGrantedAuthority(ADMIN_ROLE_NAME);
    public static final GrantedAuthority SYSTEM_ROLE = new SimpleGrantedAuthority(SYSTEM_ROLE_NAME);

    public RestUserDetails currentUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && isUserAuthenticated()) {
            return (RestUserDetails) auth.getPrincipal();
        }
        return null;
    }

    public String currentUserId() {
        RestUserDetails restUserDetails = currentUserDetails();
        return restUserDetails != null ? restUserDetails.getUserId() : null;
    }

    public boolean isUserAuthenticated() {
        return isAuthenticatedEntityInRole(USER_ROLE);
    }

    public boolean isSystemAuthenticated() {
        return isAuthenticatedEntityInRole(SYSTEM_ROLE);
    }

    public boolean isCurrentUserAdmin() {
        return isAuthenticatedEntityInRole(ADMIN_ROLE);
    }

    /**
     * Can be used for users and systems. Authenticated users will always have ROLE_USER, system will have ROLE_SYSTEM
     */
    public boolean isAuthenticatedEntityInRole(GrantedAuthority role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().contains(role);
    }
}

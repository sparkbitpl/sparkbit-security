package pl.sparkbit.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import pl.sparkbit.security.rest.user.RestUserDetails;

@Component
@SuppressWarnings({"unused", "WeakerAccess"})
public class Security {

    public static final String AUTH_TOKEN_HEADER = "X-Sparkbit-Auth-Token";

    public static final String AUTH_TOKEN_COOKIE_NAME = "sparkbitAuthToken";

    public static final String ADMIN_ROLE_NAME = "ROLE_ADMIN";

    public static final String EXTERNAL_SYSTEM_ROLE_NAME = "ROLE_EXTERNAL_SYSTEM";

    public static final GrantedAuthority ADMIN_ROLE = new SimpleGrantedAuthority(ADMIN_ROLE_NAME);

    public static final GrantedAuthority EXTERNAL_SYSTEM_ROLE = new SimpleGrantedAuthority(EXTERNAL_SYSTEM_ROLE_NAME);


    public RestUserDetails currentUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && !auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
            return (RestUserDetails) auth.getPrincipal();
        }
        return null;
    }

    public String currentUserId() {
        RestUserDetails restUserDetails = currentUserDetails();
        return restUserDetails != null ? restUserDetails.getUserId() : null;
    }

    public boolean isCurrentUserAdmin() {
        return isUserInRole(currentUserDetails(), ADMIN_ROLE);
    }

    private boolean isUserInRole(UserDetails userDetails, GrantedAuthority role) {
        return userDetails != null && userDetails.getAuthorities().contains(role);
    }
}

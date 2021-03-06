package pl.sparkbit.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.sparkbit.security.domain.RestUserDetails;
import pl.sparkbit.security.login.LoginUserDetails;

@SuppressWarnings({"unused", "WeakerAccess"})
@Slf4j
public class Security {

    public static final String USER_ROLE_NAME = "ROLE_USER";
    public static final String ADMIN_ROLE_NAME = "ROLE_ADMIN";
    public static final String SYSTEM_ROLE_NAME = "ROLE_SYSTEM";
    public static final GrantedAuthority USER_ROLE = new SimpleGrantedAuthority(USER_ROLE_NAME);
    public static final GrantedAuthority ADMIN_ROLE = new SimpleGrantedAuthority(ADMIN_ROLE_NAME);
    public static final GrantedAuthority SYSTEM_ROLE = new SimpleGrantedAuthority(SYSTEM_ROLE_NAME);

    // This method is useful for endpoints other than login
    public RestUserDetails currentUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && isUserAuthenticated()) {
            return (RestUserDetails) auth.getPrincipal();
        }
        return null;
    }

    public String currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof RestUserDetails) {
            if (auth.isAuthenticated() && isEntityInRole(auth, USER_ROLE)) {
                return ((RestUserDetails) principal).getUserId();
            }
            log.warn("Unauthenticated user with RestUserDetails principal - should never happen");
        } else if (principal instanceof LoginUserDetails) {
            if (auth.isAuthenticated()) {
                return ((LoginUserDetails) principal).getUserId();
            }
            log.warn("Unauthenticated user with LoginUserDetails principal - should never happen");
        } else {
            log.warn("Unexpected type of authenticated principal");
        }
        return null;
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
     * Can be used for users and systems. Authenticated users will always have ROLE_USER, systems will have ROLE_SYSTEM
     */
    public boolean isAuthenticatedEntityInRole(GrantedAuthority role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return isEntityInRole(auth, role);
    }

    public boolean isEntityInRole(Authentication auth, GrantedAuthority role) {
        return auth != null && auth.getAuthorities().contains(role);
    }
}

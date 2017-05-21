package pl.sparkbit.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.sparkbit.security.rest.RestUserDetails;

@Component
public class Security {

    public static final String AUTH_TOKEN_HEADER = "X-Sparkbit-Auth-Token";

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
}

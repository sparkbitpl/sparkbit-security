package pl.sparkbit.security.restauthn;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;
import pl.sparkbit.security.domain.RestUserDetails;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class RestAuthenticationFilter extends GenericFilterBean {

    private final AuthenticationManager authenticationManager;
    private final AuthenticationEntryPoint entryPoint;
    private final AuthenticationTokenHelper authenticationTokenHelper;
    private final String sessionValidTimeHeaderName;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        Optional<String> authToken = authenticationTokenHelper.extractAuthenticationToken(request);
        if (authToken.isPresent()) {
            try {
                RestAuthenticationToken token = new RestAuthenticationToken(authToken.get());
                Authentication authentication = authenticationManager.authenticate(token);
                Assert.isTrue(authentication.isAuthenticated(),
                        "Authentication is not authenticated after successful authentication");
                SecurityContextHolder.getContext().setAuthentication(authentication);

                RestUserDetails restUserDetails = (RestUserDetails) authentication.getPrincipal();
                String validTsAsString = getValidTsAsString(restUserDetails);
                response.setHeader(sessionValidTimeHeaderName, validTsAsString);
            } catch (AuthenticationException failed) {
                SecurityContextHolder.clearContext();
                entryPoint.commence(request, response, failed);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getValidTsAsString(RestUserDetails restUserDetails) {
        if (restUserDetails.getExpiresAt() == null) {
            return null;
        }

        return String.valueOf(restUserDetails.getExpiresAt().toEpochMilli());
    }
}

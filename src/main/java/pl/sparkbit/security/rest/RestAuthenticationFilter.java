package pl.sparkbit.security.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static pl.sparkbit.security.Security.AUTH_TOKEN_HEADER;

@RequiredArgsConstructor
public class RestAuthenticationFilter extends GenericFilterBean {

    private final AuthenticationManager authenticationManager;
    private final AuthenticationEntryPoint entryPoint;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        Optional<String> authToken = getAuthToken(request);
        if (authToken.isPresent()) {
            try {
                AuthTokenAuthenticationToken token = new AuthTokenAuthenticationToken(authToken.get());
                Authentication authentication = authenticationManager.authenticate(token);
                Assert.isTrue(authentication.isAuthenticated(),
                        "Authentication is not authenticated after successful authentication");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (AuthenticationException failed) {
                SecurityContextHolder.clearContext();
                entryPoint.commence(request, response, failed);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private Optional<String> getAuthToken(HttpServletRequest request) throws AuthenticationException {
        String authToken = request.getHeader(AUTH_TOKEN_HEADER);
        return Optional.ofNullable(authToken);
    }
}

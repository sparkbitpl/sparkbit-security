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

import static pl.sparkbit.security.Security.SESSION_ID_HEADER;

@RequiredArgsConstructor
public class RestAuthenticationFilter extends GenericFilterBean {

    private final AuthenticationManager authenticationManager;
    private final AuthenticationEntryPoint entryPoint;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        try {
            String sessionId = getSessionId(request);
            SessionIdAuthenticationToken token = new SessionIdAuthenticationToken(sessionId);
            Authentication authentication = authenticationManager.authenticate(token);
            Assert.isTrue(authentication.isAuthenticated(),
                    "Authentication is not authenticated after successful authentication");
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException failed) {
            SecurityContextHolder.clearContext();
            entryPoint.commence(request, response, failed);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getSessionId(HttpServletRequest request) throws AuthenticationException {
        String sessionId = request.getHeader(SESSION_ID_HEADER);
        if (sessionId == null || sessionId.isEmpty()) {
            throw new MissingSessionIdHeaderException(SESSION_ID_HEADER + " is mandatory");
        }
        return sessionId;
    }
}

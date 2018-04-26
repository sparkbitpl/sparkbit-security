package pl.sparkbit.security.login;

import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.sparkbit.security.restauthn.AuthenticationTokenHelper;
import pl.sparkbit.security.service.SessionService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;

@RequiredArgsConstructor
public class SessionExpirationHeaderFilter extends OncePerRequestFilter {

    private final SessionService sessionService;
    private final String sessionExpirationTimestampHeaderName;
    private final AuthenticationTokenHelper authenticationTokenHelper;

    @Override
    @SuppressWarnings("NullableProblems")
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (sessionService.isSessionExpirationEnabled()) {
            addSessionExpirationHeader(request, response);
        }
        chain.doFilter(request, response);
    }

    private void addSessionExpirationHeader(HttpServletRequest request, HttpServletResponse response) {
        authenticationTokenHelper.extractAuthenticationToken(request).ifPresent((token) -> {
            Instant expirationTimestamp = sessionService.updateAndGetSessionExpirationTimestamp(token);
            response.setHeader(
                    sessionExpirationTimestampHeaderName, String.valueOf(expirationTimestamp.toEpochMilli()));
        });
    }
}

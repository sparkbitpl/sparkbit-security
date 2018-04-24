package pl.sparkbit.security.login;

import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;
import pl.sparkbit.security.restauthn.AuthenticationTokenHelper;
import pl.sparkbit.security.service.SessionService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@RequiredArgsConstructor
public class SessionExpirationHeaderFilter extends GenericFilterBean {

    private final SessionService sessionService;
    private final String sessionExpirationTimestampHeaderName;
    private final AuthenticationTokenHelper authenticationTokenHelper;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        chain.doFilter(req, res);

        if (!sessionService.isSessionExpirationEnabled()) {
            return;
        }

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        Optional<String> authToken = authenticationTokenHelper.extractAuthenticationToken(request);

        authToken.ifPresent((token) -> {
            Instant expirationTimestamp = sessionService.updateAndGetSessionExpirationTimestamp(token);
            response.setHeader(
                    sessionExpirationTimestampHeaderName,
                    String.valueOf(expirationTimestamp.toEpochMilli()));
        });

    }
}

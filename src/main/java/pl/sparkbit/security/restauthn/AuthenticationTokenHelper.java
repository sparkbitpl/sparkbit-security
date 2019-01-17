package pl.sparkbit.security.restauthn;

import lombok.RequiredArgsConstructor;
import pl.sparkbit.security.config.SecurityProperties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
public class AuthenticationTokenHelper {

    private final SecurityProperties configuration;

    public Optional<String> extractAuthenticationToken(HttpServletRequest request) {
        String authToken = request.getHeader(configuration.getAuthTokenHeaderName());
        if (authToken == null && request.getCookies() != null) {
            authToken = Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals(configuration.getAuthCookie().getName()))
                    .findAny()
                    .map(Cookie::getValue)
                    .orElse(null);
        }
        return Optional.ofNullable(authToken);
    }

    public Cookie buildAuthenticationTokenCookie(String authToken) {
        Cookie cookie = new Cookie(configuration.getAuthCookie().getName(), authToken);
        cookie.setPath(configuration.getAuthCookie().getPath());
        if (!configuration.getAuthCookie().getAllowUnsecured()) {
            cookie.setSecure(true);
        }
        return cookie;
    }
}

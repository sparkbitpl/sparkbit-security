package pl.sparkbit.security.rest.authn;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

import static pl.sparkbit.security.Properties.AUTH_TOKEN_COOKIE_NAME;
import static pl.sparkbit.security.Properties.AUTH_TOKEN_HEADER_NAME;
import static pl.sparkbit.security.Security.DEFAULT_AUTH_TOKEN_COOKIE_NAME;
import static pl.sparkbit.security.Security.DEFAULT_AUTH_TOKEN_HEADER_NAME;

@Component
public class AuthenticationTokenHelper {

    @Value("${" + AUTH_TOKEN_HEADER_NAME + ":" + DEFAULT_AUTH_TOKEN_HEADER_NAME + "}")
    private String authTokenHeaderName;
    @Value("${" + AUTH_TOKEN_COOKIE_NAME + ":" + DEFAULT_AUTH_TOKEN_COOKIE_NAME + "}")
    private String authTokenCookieName;
    @Value("${sparkbit.security.allowUnsecuredCookie:false}")
    private boolean allowUnsecuredCookie;

    public Optional<String> extractAuthenticationToken(HttpServletRequest request) {
        String authToken = request.getHeader(authTokenHeaderName);
        if (authToken == null && request.getCookies() != null) {
            authToken = Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals(authTokenCookieName))
                    .findAny()
                    .map(Cookie::getValue)
                    .orElse(null);
        }
        return Optional.ofNullable(authToken);
    }

    public Cookie buildAuthenticationTokenCookie(String authToken) {
        Cookie cookie = new Cookie(authTokenCookieName, authToken);
        if (!allowUnsecuredCookie) {
            cookie.setSecure(true);
        }
        return cookie;
    }
}

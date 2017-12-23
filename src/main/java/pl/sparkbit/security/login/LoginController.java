package pl.sparkbit.security.login;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pl.sparkbit.security.Security;
import pl.sparkbit.security.SecurityService;
import pl.sparkbit.security.domain.Session;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static pl.sparkbit.security.Security.AUTH_TOKEN_HEADER;

@RequiredArgsConstructor
@RestController
public class LoginController {

    public static final String LOGIN = "/login";
    private static final String LOGOUT = "/logout";

    private final SecurityService securityService;
    @Value("${sparkbit.security.allowUnsecuredCookie:false}")
    private boolean allowUnsecuredCookie;

    @PostMapping(LOGIN)
    public AuthTokenDTO login(@RequestHeader(name = AUTH_TOKEN_HEADER, required = false) String oldAuthToken,
            HttpServletResponse response) {
        Session session = securityService.startNewSession(oldAuthToken);

        Cookie cookie = getAuthCookie(session.getAuthToken());
        response.addCookie(cookie);

        return new AuthTokenDTO(session.getAuthToken());
    }

    @PostMapping(LOGOUT)
    public ResponseEntity<Object> logout(HttpServletResponse response) {
        securityService.logout();

        Cookie cookie = getAuthCookie(null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return new ResponseEntity<>(NO_CONTENT);
    }

    private Cookie getAuthCookie(String authToken) {
        Cookie cookie = new Cookie(Security.AUTH_TOKEN_COOKIE_NAME, authToken);
        if (!allowUnsecuredCookie) {
            cookie.setSecure(true);
        }
        return cookie;
    }
}

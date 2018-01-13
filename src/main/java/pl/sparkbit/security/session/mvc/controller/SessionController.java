package pl.sparkbit.security.session.mvc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pl.sparkbit.security.Security;
import pl.sparkbit.security.session.domain.Session;
import pl.sparkbit.security.session.mvc.dto.out.AuthTokenDTO;
import pl.sparkbit.security.session.service.SessionService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static pl.sparkbit.security.Paths.LOGIN;
import static pl.sparkbit.security.Paths.LOGOUT;
import static pl.sparkbit.security.Security.AUTH_TOKEN_COOKIE_NAME;
import static pl.sparkbit.security.Security.AUTH_TOKEN_HEADER;

@RequiredArgsConstructor
@RestController
@SuppressWarnings("unused")
public class SessionController {

    private final SessionService sessionService;

    @Value("${sparkbit.security.allowUnsecuredCookie:false}")
    private boolean allowUnsecuredCookie;

    @PostMapping(LOGIN)
    public AuthTokenDTO login(@RequestHeader(name = AUTH_TOKEN_HEADER, required = false) String oldAuthTokenHeader,
                              @CookieValue(name = AUTH_TOKEN_COOKIE_NAME, required = false) String oldAuthTokenCookie,
                              HttpServletResponse response) {
        String oldAuthToken = oldAuthTokenHeader != null ? oldAuthTokenHeader : oldAuthTokenCookie;
        Session session = sessionService.startNewSession(oldAuthToken);

        Cookie cookie = getAuthCookie(session.getAuthToken());
        response.addCookie(cookie);

        return new AuthTokenDTO(session.getAuthToken());
    }

    @PostMapping(LOGOUT)
    public ResponseEntity<Object> logout(HttpServletResponse response) {
        sessionService.endSession();

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

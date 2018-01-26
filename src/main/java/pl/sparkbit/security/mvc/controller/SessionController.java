package pl.sparkbit.security.mvc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.sparkbit.security.domain.Session;
import pl.sparkbit.security.mvc.dto.out.AuthTokenDTO;
import pl.sparkbit.security.restauthn.AuthenticationTokenHelper;
import pl.sparkbit.security.service.SessionService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static pl.sparkbit.security.Paths.LOGIN;
import static pl.sparkbit.security.Paths.LOGOUT;

@RequiredArgsConstructor
@RestController
@SuppressWarnings("unused")
public class SessionController {

    private final AuthenticationTokenHelper authenticationTokenHelper;
    private final SessionService sessionService;

    @PostMapping(LOGIN)
    public AuthTokenDTO login(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> oldAuthToken = authenticationTokenHelper.extractAuthenticationToken(request);
        Session session = sessionService.startNewSession(oldAuthToken.orElse(null));

        Cookie cookie = authenticationTokenHelper.buildAuthenticationTokenCookie(session.getAuthToken());
        response.addCookie(cookie);

        return new AuthTokenDTO(session.getAuthToken());
    }

    @PostMapping(LOGOUT)
    @ResponseStatus(NO_CONTENT)
    public void logout(HttpServletResponse response) {
        sessionService.endSession();

        Cookie cookie = authenticationTokenHelper.buildAuthenticationTokenCookie(null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}

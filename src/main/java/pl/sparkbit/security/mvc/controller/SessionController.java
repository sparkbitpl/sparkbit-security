package pl.sparkbit.security.mvc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.sparkbit.security.domain.NewSessionData;
import pl.sparkbit.security.mvc.dto.out.NewSessionDataDTO;
import pl.sparkbit.security.restauthn.AuthenticationTokenHelper;
import pl.sparkbit.security.service.SessionService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RequiredArgsConstructor
@RestController
@SuppressWarnings("unused")
public class SessionController {

    private final AuthenticationTokenHelper authenticationTokenHelper;
    private final SessionService sessionService;

    public NewSessionDataDTO login(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> oldAuthToken = authenticationTokenHelper.extractAuthenticationToken(request);
        NewSessionData sessionData = sessionService.startNewSession(oldAuthToken.orElse(null));

        Cookie cookie = authenticationTokenHelper.buildAuthenticationTokenCookie(sessionData.getAuthToken());
        response.addCookie(cookie);

        return NewSessionDataDTO.from(sessionData);
    }

    @ResponseStatus(NO_CONTENT)
    public void logout(HttpServletResponse response) {
        sessionService.endSession();

        Cookie cookie = authenticationTokenHelper.buildAuthenticationTokenCookie(null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}

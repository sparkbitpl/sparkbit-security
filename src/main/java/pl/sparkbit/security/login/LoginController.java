package pl.sparkbit.security.login;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pl.sparkbit.security.SecurityService;
import pl.sparkbit.security.domain.Session;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static pl.sparkbit.security.Security.AUTH_TOKEN_HEADER;

@RequiredArgsConstructor
@RestController
public class LoginController {

    public static final String LOGIN = "/login";
    private static final String LOGOUT = "/logout";

    private final SecurityService securityService;

    @PostMapping(LOGIN)
    public AuthTokenDTO login(@RequestHeader(name = AUTH_TOKEN_HEADER, required = false) String oldAuthToken) {
        Session session = securityService.startNewSession(oldAuthToken);

        return new AuthTokenDTO(session.getAuthToken());
    }

    @PostMapping(LOGOUT)
    public ResponseEntity<Object> logout() {
        securityService.logout();
        return new ResponseEntity<>(NO_CONTENT);
    }
}

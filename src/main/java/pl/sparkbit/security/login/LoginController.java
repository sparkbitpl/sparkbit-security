package pl.sparkbit.security.login;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pl.sparkbit.security.SecurityService;
import pl.sparkbit.security.domain.Session;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static pl.sparkbit.security.Security.SESSION_ID_HEADER;

@RequiredArgsConstructor
@RestController
public class LoginController {

    public static final String LOGIN = "/login";
    private static final String LOGOUT = "/logout";

    private final SecurityService securityService;

    @PostMapping(LOGIN)
    public SessionIdDTO login(@RequestHeader(name = SESSION_ID_HEADER, required = false) String oldSessionId) {
        Session session = securityService.startNewSession(oldSessionId);

        return new SessionIdDTO(session.getId());
    }

    @PostMapping(LOGOUT)
    public ResponseEntity<Object> logout() {
        securityService.logout();
        return new ResponseEntity<>(NO_CONTENT);
    }
}

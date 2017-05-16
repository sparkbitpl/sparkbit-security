package pl.sparkbit.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import pl.sparkbit.commons.util.IdGenerator;
import pl.sparkbit.security.dao.SecurityDao;
import pl.sparkbit.security.domain.Session;
import pl.sparkbit.security.login.LoginPrincipal;
import pl.sparkbit.security.login.LoginUserDetails;
import pl.sparkbit.security.rest.RestUserDetails;
import pl.sparkbit.security.rest.SessionNotFoundException;

import java.time.Clock;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class SecurityServiceImpl implements SecurityService, UserDetailsService {

    private final SecurityDao securityDao;
    private final IdGenerator idGenerator;
    private final Clock clock;
    private final Security security;

    @Override
    @Transactional
    //todo should be cacheable
    public UserDetails loadUserByUsername(String principalData) throws UsernameNotFoundException {
        LoginPrincipal principal = new LoginPrincipal(principalData);
        Optional<LoginUserDetails> userDetails =
                securityDao.selectLoginUserDetails(principal.getAuthnAttributes());
        return userDetails.orElseThrow(() -> new UsernameNotFoundException(principal + " not found"));
    }

    @Override
    public Session startNewSession(String oldSessionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Assert.notNull(auth, "Can't create session for unauthenticated user");
        Assert.isInstanceOf(LoginUserDetails.class, auth.getPrincipal(),
                "New session can be started only from login endpoint");
        LoginUserDetails loginUserDetails = (LoginUserDetails) auth.getPrincipal();

        Session newSession = Session.builder()
                .id(idGenerator.generate())
                .userId(loginUserDetails.getUserId())
                .creation(clock.instant())
                .build();
        securityDao.insertSession(newSession);

        if (oldSessionId != null && !oldSessionId.isEmpty()) {
            Optional<Session> oldSession = securityDao.selectSession(oldSessionId);
            if (oldSession.isPresent()) {
                if (oldSession.get().getUserId().equals(newSession.getUserId())) {
                    log.info("Login request with X-Sid header - should never happened for correctly implemented " +
                            "clients. Invalidating old session for user {}", newSession.getUserId());
                    securityDao.deleteSession(oldSessionId);
                } else {
                    log.info("Login request from user {} with X-Sid header from another user {}",
                            newSession.getUserId(), oldSession.get().getUserId());
                }
            } else {
                log.info("Login request with X-Sid header that is not present in database");
            }
        }

        return newSession;
    }

    @Override
    public RestUserDetails retrieveRestUserDetails(String sessionId) {
        Optional<RestUserDetails> restUserDetails = securityDao.selectRestUserDetails(sessionId);
        return restUserDetails.orElseThrow(() -> new SessionNotFoundException("Session not found"));
    }

    @Override
    public void logout() {
        RestUserDetails restUserDetails = security.currentUserDetails();
        securityDao.deleteSession(restUserDetails.getSessionId());
        SecurityContextHolder.clearContext();
    }
}

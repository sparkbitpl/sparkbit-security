package pl.sparkbit.security.session.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import pl.sparkbit.commons.util.IdGenerator;
import pl.sparkbit.security.Security;
import pl.sparkbit.security.rest.user.RestUserDetails;
import pl.sparkbit.security.session.auth.LoginPrincipal;
import pl.sparkbit.security.session.auth.LoginUserDetails;
import pl.sparkbit.security.session.dao.SessionDao;
import pl.sparkbit.security.session.domain.Session;
import pl.sparkbit.security.session.service.SessionService;

import java.time.Clock;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class SessionServiceImpl implements SessionService {

    private final SessionDao sessionDao;
    private final IdGenerator idGenerator;
    private final Clock clock;
    private final Security security;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String principalData) throws UsernameNotFoundException {
        LoginPrincipal principal = new LoginPrincipal(principalData);
        Optional<LoginUserDetails> userDetails =
                sessionDao.selectLoginUserDetails(principal.getAuthnAttributes());
        return userDetails.orElseThrow(() -> new UsernameNotFoundException(principal + " not found"));
    }

    @Override
    public Session startNewSession(String oldAuthToken) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Assert.notNull(auth, "Can't create session for unauthenticated user");
        Assert.isInstanceOf(LoginUserDetails.class, auth.getPrincipal(),
                "New session can be started only from login endpoint");
        LoginUserDetails loginUserDetails = (LoginUserDetails) auth.getPrincipal();

        Session newSession = Session.builder()
                .authToken(idGenerator.generate())
                .userId(loginUserDetails.getUserId())
                .creation(clock.instant())
                .build();
        sessionDao.insertSession(newSession);

        if (oldAuthToken != null && !oldAuthToken.isEmpty()) {
            Optional<Session> oldSession = sessionDao.selectSession(oldAuthToken);
            if (oldSession.isPresent()) {
                if (oldSession.get().getUserId().equals(newSession.getUserId())) {
                    log.info("Login request with auth token provided - should never happened for correctly " +
                            "implemented clients. Invalidating old session for user {}", newSession.getUserId());
                    sessionDao.deleteSession(oldAuthToken, clock.instant());
                } else {
                    log.info("Login request from user {} with auth token from another user {}",
                            newSession.getUserId(), oldSession.get().getUserId());
                }
            } else {
                log.info("Login request with auth token that is not present in database");
            }
        }

        return newSession;
    }

    @Override
    public void logout() {
        RestUserDetails restUserDetails = security.currentUserDetails();
        sessionDao.deleteSession(restUserDetails.getAuthToken(), clock.instant());
        SecurityContextHolder.clearContext();
    }
}

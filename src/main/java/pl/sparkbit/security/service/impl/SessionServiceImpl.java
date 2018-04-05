package pl.sparkbit.security.service.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import pl.sparkbit.security.Security;
import pl.sparkbit.security.dao.SessionDao;
import pl.sparkbit.security.domain.RestUserDetails;
import pl.sparkbit.security.domain.Session;
import pl.sparkbit.security.login.LoginUserDetails;
import pl.sparkbit.security.service.ExtraAuthnCheckService;
import pl.sparkbit.security.service.SessionService;
import pl.sparkbit.security.util.SecureRandomStringGenerator;

import javax.annotation.PostConstruct;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;
import static pl.sparkbit.security.config.Properties.*;

@RequiredArgsConstructor
@Service
@Slf4j
@SuppressWarnings("unused")
public class SessionServiceImpl implements SessionService {

    private static final int AUTH_TOKEN_LENGTH = 32;
    private static final int DEFAULT_SESSION_EXPIRATION_MINUTES = 60;

    private final SessionDao sessionDao;
    private final Clock clock;
    private final Security security;
    private final SecureRandomStringGenerator secureRandomStringGenerator;
    private final ApplicationContext applicationContext;

    private ExtraAuthnCheckService extraAuthnCheckService;

    @Value("${" + EXTRA_AUTHENTICATION_CHECK_ENABLED + ":false}")
    private boolean extraAuthenticationCheckEnabled;

    @Getter
    @Value("${" + SESSION_EXPIRATION_ENABLED + ":false}")
    private boolean sessionExpirationEnabled;

    @Value("${" + SESSION_EXPIRATION_MINUTES + ":" + DEFAULT_SESSION_EXPIRATION_MINUTES + "}")
    private Integer sessionExpirationMinutes;

    @PostConstruct
    private void setup() {
        if (extraAuthenticationCheckEnabled) {
            extraAuthnCheckService = applicationContext.getBean(ExtraAuthnCheckService.class);
        }
    }

    @Override
    @Transactional
    public Session startNewSession(String oldAuthToken) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Assert.notNull(auth, "Can't create session for unauthenticated user");
        Assert.isInstanceOf(LoginUserDetails.class, auth.getPrincipal(),
                "New session can be started only from login endpoint");
        LoginUserDetails loginUserDetails = (LoginUserDetails) auth.getPrincipal();
        String userId = loginUserDetails.getUserId();

        Session newSession = Session.builder()
                .authToken(secureRandomStringGenerator.base58String(AUTH_TOKEN_LENGTH))
                .userId(userId)
                .creationTimestamp(clock.instant())
                .expirationTimestamp(getExpirationTimestamp())
                .build();
        sessionDao.insertSession(newSession);

        if (extraAuthenticationCheckEnabled) {
            sessionDao.updateExtraAuthnCheckRequired(newSession.getAuthToken(), true);
            extraAuthnCheckService.initiateExtraAuthnCheck(userId);
        }

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
    @Transactional
    public void endSession() {
        RestUserDetails restUserDetails = security.currentUserDetails();
        sessionDao.deleteSession(restUserDetails.getAuthToken(), clock.instant());
        SecurityContextHolder.clearContext();
    }

    @Override
    @Transactional
    public void endAllSessionsForUser(String userId) {
        sessionDao.deleteSessions(userId, clock.instant());
    }

    @Override
    @Transactional
    public void updateSessionExpirationTimestamp(String authToken) {
        Instant expirationTimestamp = getExpirationTimestamp();
        sessionDao.updateSessionExpirationTimestamp(authToken, expirationTimestamp);
        RequestContextHolder.currentRequestAttributes()
                .setAttribute(SESSION_EXPIRATION_TIMESTAMP_REQUEST_ATTRIBUTE, expirationTimestamp, SCOPE_REQUEST);
    }

    private Instant getExpirationTimestamp() {
        if (!isSessionExpirationEnabled()) {
            return null;
        }

        return clock.instant().plus(sessionExpirationMinutes, ChronoUnit.MINUTES);
    }
}

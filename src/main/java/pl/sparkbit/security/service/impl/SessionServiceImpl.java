package pl.sparkbit.security.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import pl.sparkbit.security.Security;
import pl.sparkbit.security.callbacks.LoginResponseAdditionalDataCallback;
import pl.sparkbit.security.config.SecurityProperties;
import pl.sparkbit.security.dao.SessionDao;
import pl.sparkbit.security.domain.NewSessionData;
import pl.sparkbit.security.domain.RestUserDetails;
import pl.sparkbit.security.domain.Session;
import pl.sparkbit.security.hooks.LoginHook;
import pl.sparkbit.security.hooks.LogoutHook;
import pl.sparkbit.security.login.LoginUserDetails;
import pl.sparkbit.security.password.encoder.AuthTokenHasher;
import pl.sparkbit.security.service.ExtraAuthnCheckService;
import pl.sparkbit.security.service.SessionService;
import pl.sparkbit.security.util.SecureRandomStringGenerator;

import javax.annotation.PostConstruct;
import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings({"unused"})
public class SessionServiceImpl implements SessionService {

    private static final int AUTH_TOKEN_LENGTH = 32;

    private final SessionDao sessionDao;
    private final AuthTokenHasher authTokenHasher;
    private final Clock clock;
    private final Security security;
    private final SecureRandomStringGenerator secureRandomStringGenerator;
    private final SecurityProperties configuration;
    private final ObjectProvider<LoginHook> loginHook;
    private final ObjectProvider<LogoutHook> logoutHook;
    private final ObjectProvider<LoginResponseAdditionalDataCallback> additionalDataCallback;
    private final ObjectProvider<ExtraAuthnCheckService> extraAuthnCheckService;

    @PostConstruct
    private void setup() {
        if (configuration.getExtraAuthnCheck().getEnabled()) {
            if (extraAuthnCheckService.getIfAvailable() == null) {
                throw new RuntimeException("No defined bean " + ExtraAuthnCheckService.class.getSimpleName());
            }
        }
    }

    @Override
    public boolean isSessionExpirationEnabled() {
        return configuration.getSessionExpiration().getEnabled();
    }

    @Override
    @Transactional
    public NewSessionData startNewSession(String oldAuthToken) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Assert.notNull(auth, "Can't create session for unauthenticated user");
        Assert.isInstanceOf(LoginUserDetails.class, auth.getPrincipal(),
                "New session can be started only from login endpoint");
        LoginUserDetails loginUserDetails = (LoginUserDetails) auth.getPrincipal();
        String userId = loginUserDetails.getUserId();

        if (oldAuthToken != null && !oldAuthToken.isEmpty()) {
            maybeDeleteOldSession(oldAuthToken, userId);
        }

        String newAuthToken = secureRandomStringGenerator.base58String(AUTH_TOKEN_LENGTH);

        Session newSession = Session.builder()
                .authTokenHash(authTokenHasher.hash(newAuthToken))
                .userId(userId)
                .creationTimestamp(clock.instant())
                .expirationTimestamp(getExpirationTimestamp())
                .build();
        sessionDao.insertSession(newSession);

        if (configuration.getExtraAuthnCheck().getEnabled()) {
            sessionDao.updateExtraAuthnCheckRequired(newSession.getAuthTokenHash(), true);
            ExtraAuthnCheckService svc = extraAuthnCheckService.getIfAvailable();
            if (svc == null) {
                throw new RuntimeException("Missing bean " + ExtraAuthnCheckService.class.getSimpleName());
            } else {
                svc.initiateExtraAuthnCheck(userId);
            }
        } else {
            loginHook.ifAvailable(hook -> hook.doAfterSuccessfulLogin(userId));
        }

        NewSessionData.NewSessionDataBuilder sessionDataBuilder = NewSessionData.builder()
                .authToken(newAuthToken)
                .userId(userId);

        if (!configuration.getExtraAuthnCheck().getEnabled()) {
            additionalDataCallback.ifAvailable(callback -> {
                Map<String, Object> additionalData = callback.getAdditionalData();
                sessionDataBuilder.additionalData(additionalData);
            });
        }
        return sessionDataBuilder.build();
    }

    @Override
    @Transactional
    public void endSession() {
        RestUserDetails restUserDetails = security.currentUserDetails();

        sessionDao.deleteSession(restUserDetails.getAuthTokenHash(), clock.instant());
        SecurityContextHolder.clearContext();

        logoutHook.ifAvailable(hook -> hook.doAfterSuccessfulLogout(restUserDetails.getUserId()));
    }

    @Override
    @Transactional
    public void endAllSessionsForUser(String userId) {
        sessionDao.deleteSessions(userId, clock.instant());
    }

    @Override
    @Transactional
    public Instant updateAndGetSessionExpirationTimestamp(String authToken) {
        Instant expirationTimestamp = getExpirationTimestamp();
        String authTokenHash = authTokenHasher.hash(authToken);

        sessionDao.updateSessionExpirationTimestamp(authTokenHash, expirationTimestamp);

        return expirationTimestamp;
    }

    private void maybeDeleteOldSession(String oldAuthToken, String userId) {

        String authTokenHash = authTokenHasher.hash(oldAuthToken);
        Optional<Session> oldSession = sessionDao.selectSession(authTokenHash);

        if (oldSession.isPresent()) {
            if (oldSession.get().getUserId().equals(userId)) {
                log.info("Login request with auth token provided - should never happened for correctly " +
                        "implemented clients. Invalidating old session for user {}", userId);
                sessionDao.deleteSession(oldAuthToken, clock.instant());
            } else {
                log.info("Login request from user {} with auth token from another user {}",
                        userId, oldSession.get().getUserId());
            }
        } else {
            log.info("Login request with auth token that is not present in database");
        }

    }

    private Instant getExpirationTimestamp() {
        if (!isSessionExpirationEnabled()) {
            return null;
        }

        return clock.instant().plus(configuration.getSessionExpiration().getDuration());
    }
}

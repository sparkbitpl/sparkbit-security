package pl.sparkbit.security.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sparkbit.security.Security;
import pl.sparkbit.security.callbacks.ExtraAuthnCheckChallengeCallback;
import pl.sparkbit.security.dao.SessionDao;
import pl.sparkbit.security.domain.SecurityChallenge;
import pl.sparkbit.security.hooks.LoginHook;
import pl.sparkbit.security.mvc.dto.in.ExtraAuthnCheckDTO;
import pl.sparkbit.security.service.ExtraAuthnCheckService;
import pl.sparkbit.security.util.SecurityChallenges;

import java.util.Optional;

import static pl.sparkbit.security.config.SecurityProperties.EXTRA_AUTHENTICATION_CHECK_ENABLED;
import static pl.sparkbit.security.domain.SecurityChallengeType.EXTRA_AUTHN_CHECK;

@ConditionalOnProperty(value = EXTRA_AUTHENTICATION_CHECK_ENABLED, havingValue = "true")
@RequiredArgsConstructor
@Service
@Slf4j
@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "unused"})
public class ExtraAuthnCheckServiceImpl implements ExtraAuthnCheckService {

    private static final int DEFAULT_CHALLENGE_VALIDITY_HOURS = 1;

    private final ExtraAuthnCheckChallengeCallback callback;
    private final Optional<LoginHook> loginHook;
    private final Security security;
    private final SecurityChallenges securityChallenges;
    private final SessionDao sessionDao;

    @Override
    @Transactional
    public void initiateExtraAuthnCheck(String userId) {
        SecurityChallenge challenge = securityChallenges.createAndInsertChallenge(userId, EXTRA_AUTHN_CHECK);
        callback.transmitToUser(challenge);
        log.trace("Extra authentication check initiated for user {}", userId);
    }

    @Override
    @Transactional
    public void performExtraAuthnCheck(ExtraAuthnCheckDTO dto) {
        String token = dto.getToken();
        SecurityChallenge challenge = securityChallenges.finishChallenge(token, EXTRA_AUTHN_CHECK);
        callback.notifyOfSuccess(challenge);

        String authTokenHash = security.currentUserDetails().getAuthTokenHash();
        sessionDao.updateExtraAuthnCheckRequired(authTokenHash, false);

        log.debug("Extra authn check succeeded for user {}", challenge.getUserId());

        loginHook.ifPresent(hook -> hook.doAfterSuccessfulLogin(challenge.getUserId()));
    }
}

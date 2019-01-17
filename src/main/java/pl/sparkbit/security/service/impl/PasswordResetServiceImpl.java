package pl.sparkbit.security.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sparkbit.commons.exception.NotFoundException;
import pl.sparkbit.security.callbacks.PasswordResetChallengeCallback;
import pl.sparkbit.security.callbacks.SetNewPasswordChallengeCallback;
import pl.sparkbit.security.config.SecurityProperties;
import pl.sparkbit.security.dao.CredentialsDao;
import pl.sparkbit.security.dao.UserDetailsDao;
import pl.sparkbit.security.domain.Credentials;
import pl.sparkbit.security.domain.SecurityChallenge;
import pl.sparkbit.security.domain.SecurityChallengeType;
import pl.sparkbit.security.exception.NoValidTokenFoundException;
import pl.sparkbit.security.login.AuthnAttributes;
import pl.sparkbit.security.login.ExpectedAndProvidedAuthnAttributesMismatchException;
import pl.sparkbit.security.login.LoginPrincipal;
import pl.sparkbit.security.login.LoginPrincipalFactory;
import pl.sparkbit.security.service.PasswordResetService;
import pl.sparkbit.security.util.SecurityChallenges;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.transaction.annotation.Propagation.MANDATORY;
import static pl.sparkbit.security.config.SecurityProperties.PASSWORD_RESET_ENABLED;
import static pl.sparkbit.security.domain.SecurityChallengeType.PASSWORD_RESET;
import static pl.sparkbit.security.domain.SecurityChallengeType.SET_NEW_PASSWORD;
import static pl.sparkbit.security.exception.NoValidTokenFoundException.FailureReason.TOKEN_NOT_FOUND;

@ConditionalOnProperty(value = PASSWORD_RESET_ENABLED, havingValue = "true")
@RequiredArgsConstructor
@Service
@Slf4j
@SuppressWarnings("unused")
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final List<SecurityChallengeType> ALLOWED_TYPES_TO_RESET =
            Arrays.asList(PASSWORD_RESET, SET_NEW_PASSWORD);

    private final CredentialsDao credentialsDao;
    private final LoginPrincipalFactory loginPrincipalFactory;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<SetNewPasswordChallengeCallback> setNewPasswordCallback;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetChallengeCallback passwordResetCallback;
    private final SecurityChallenges securityChallenges;
    private final UserDetailsDao userDetailsDao;
    private final SecurityProperties configuration;

    @Override
    @Transactional
    public void initiatePasswordReset(Map<String, String> authnAttributesMap) {
        LoginPrincipal loginPrincipal;
        try {
            loginPrincipal = loginPrincipalFactory.generate(authnAttributesMap);
        } catch (ExpectedAndProvidedAuthnAttributesMismatchException e) {
            throw new AccessDeniedException(e.getMessage());
        }
        AuthnAttributes authnAttributes = loginPrincipal.getAuthnAttributes();

        Optional<String> userIdOpt = userDetailsDao.selectUserId(authnAttributes);
        if (!userIdOpt.isPresent()) {
            log.debug("Initiated password reset for non-existent user {}", authnAttributes);
            if (configuration.getPasswordReset().getInformNotFound()) {
                throw new NotFoundException("User not found");
            }
            return;
        }

        String userId = userIdOpt.get();
        SecurityChallenge challenge = securityChallenges.createAndInsertChallenge(userId, PASSWORD_RESET);
        passwordResetCallback.transmitToUser(challenge);
        log.debug("User {} initiated password reset", userId);
    }

    @Override
    @Transactional(propagation = MANDATORY)
    public void initiateSetNewPassword(String userId) {
        if (!setNewPasswordCallback.isPresent()) {
            throw new IllegalStateException("Implement SetNewPasswordChallengeCallback bean!");
        }
        SecurityChallenge challenge = securityChallenges.createAndInsertChallenge(userId, SET_NEW_PASSWORD);
        setNewPasswordCallback.get().transmitToUser(challenge);
        log.debug("User {} initiated setting new password", userId);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String password, SecurityChallengeType resetType) {
        if (!ALLOWED_TYPES_TO_RESET.contains(resetType)) {
            log.debug("Illegal token type", resetType);
            throw new NoValidTokenFoundException("Valid token not found", TOKEN_NOT_FOUND);
        }

        SecurityChallenge challenge = securityChallenges.finishChallenge(token, resetType);
        if (SET_NEW_PASSWORD.equals(resetType)) {
            setNewPasswordCallback.ifPresent(c -> c.notifyOfSuccess(challenge));
        } else if (PASSWORD_RESET.equals(resetType)) {
            passwordResetCallback.notifyOfSuccess(challenge);
        }

        String encodedPassword = passwordEncoder.encode(password);
        Credentials credentials = Credentials.builder().userId(challenge.getUserId()).password(encodedPassword).build();
        credentialsDao.updateCredentials(credentials);

        log.debug("Password for user {} changed", challenge.getUserId());
    }
}

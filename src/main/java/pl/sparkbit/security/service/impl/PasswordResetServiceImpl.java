package pl.sparkbit.security.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sparkbit.commons.exception.NotFoundException;
import pl.sparkbit.commons.util.IdGenerator;
import pl.sparkbit.security.callbacks.PasswordResetChallengeCallback;
import pl.sparkbit.security.dao.CredentialsDao;
import pl.sparkbit.security.dao.SecurityChallengeDao;
import pl.sparkbit.security.dao.UserDetailsDao;
import pl.sparkbit.security.domain.Credentials;
import pl.sparkbit.security.domain.SecurityChallenge;
import pl.sparkbit.security.exception.NoValidTokenFoundException;
import pl.sparkbit.security.login.AuthnAttributes;
import pl.sparkbit.security.service.PasswordResetService;
import pl.sparkbit.security.util.SecurityChallengeTokenGenerator;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static pl.sparkbit.security.config.Properties.PASSWORD_RESET_CHALLENGE_INFORM_NOT_FOUND;
import static pl.sparkbit.security.config.Properties.PASSWORD_RESET_CHALLENGE_VALIDITY_HOURS;
import static pl.sparkbit.security.config.Properties.PASSWORD_RESET_ENABLED;
import static pl.sparkbit.security.domain.SecurityChallengeType.PASSWORD_RESET;
import static pl.sparkbit.security.exception.NoValidTokenFoundException.FailureReason.TOKEN_EXPIRED;
import static pl.sparkbit.security.exception.NoValidTokenFoundException.FailureReason.TOKEN_NOT_FOUND;

@ConditionalOnProperty(value = PASSWORD_RESET_ENABLED, havingValue = "true")
@RequiredArgsConstructor
@Service
@Slf4j
@SuppressWarnings("unused")
public class PasswordResetServiceImpl implements PasswordResetService {

    private final IdGenerator idGenerator;
    private final SecurityChallengeTokenGenerator securityChallengeTokenGenerator;
    private final Clock clock;
    private final SecurityChallengeDao securityChallengeDao;
    private final CredentialsDao credentialsDao;
    private final PasswordResetChallengeCallback callback;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsDao userDetailsDao;

    @Value("${" + PASSWORD_RESET_CHALLENGE_VALIDITY_HOURS + ":1}")
    private int challengeValidityHours;
    @Value("${" + PASSWORD_RESET_CHALLENGE_INFORM_NOT_FOUND + ":false}")
    private boolean informNotFound;

    @Override
    @Transactional
    public void initiatePasswordReset(AuthnAttributes authnAttributes) {
        Optional<String> userIdOpt = userDetailsDao.selectUserId(authnAttributes);
        if (!userIdOpt.isPresent()) {
            log.debug("Initiated password reset for non-existent user {}", authnAttributes);
            if (informNotFound) {
                throw new NotFoundException("User not found");
            }
            return;
        }

        String userId = userIdOpt.get();
        String id = idGenerator.generate();
        String token = securityChallengeTokenGenerator.generateChallengeToken();
        Instant expirationTimestamp = clock.instant().plus(challengeValidityHours, ChronoUnit.HOURS);

        SecurityChallenge challenge = SecurityChallenge.builder()
                .id(id)
                .userId(userId)
                .type(PASSWORD_RESET)
                .token(token)
                .expirationTimestamp(expirationTimestamp)
                .build();

        securityChallengeDao.deleteChallenge(userId, PASSWORD_RESET);
        securityChallengeDao.insertChallenge(challenge);
        log.debug("User {} initiated password reset", userId);

        callback.transmitToUser(challenge);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String password) {
        Optional<SecurityChallenge> challengeOpt =
                securityChallengeDao.selectChallengeByTokenAndType(token, PASSWORD_RESET);
        if (!challengeOpt.isPresent()) {
            log.debug("Security challenge with token {} not found", token);
            throw new NoValidTokenFoundException("Valid token not found", TOKEN_NOT_FOUND);
        }
        SecurityChallenge challenge = challengeOpt.get();

        if (challenge.getExpirationTimestamp().isBefore(clock.instant())) {
            log.debug("Security challenge with token {} expired on {}", token,
                    challenge.getExpirationTimestamp().toEpochMilli());
            throw new NoValidTokenFoundException("Valid token not found", TOKEN_EXPIRED);
        }

        securityChallengeDao.deleteChallenge(challenge.getId());
        log.debug("Password for user {} changed", challenge.getUserId());

        String encodedPassword = passwordEncoder.encode(password);
        Credentials credentials = Credentials.builder().userId(challenge.getUserId()).password(encodedPassword).build();
        credentialsDao.updateCredentials(credentials);

        callback.notifyOfSuccess(challenge);
    }
}

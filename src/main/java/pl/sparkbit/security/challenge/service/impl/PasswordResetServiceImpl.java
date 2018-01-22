package pl.sparkbit.security.challenge.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import pl.sparkbit.commons.util.IdGenerator;
import pl.sparkbit.security.challenge.callbacks.PasswordResetChallengeCallback;
import pl.sparkbit.security.challenge.dao.SecurityChallengeDao;
import pl.sparkbit.security.challenge.domain.SecurityChallenge;
import pl.sparkbit.security.challenge.exception.NoValidTokenFoundException;
import pl.sparkbit.security.challenge.service.PasswordResetService;
import pl.sparkbit.security.challenge.util.SecurityChallengeTokenGenerator;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static pl.sparkbit.security.challenge.domain.SecurityChallengeType.PASSWORD_RESET;
import static pl.sparkbit.security.challenge.exception.NoValidTokenFoundException.FailureReason.TOKEN_EXPIRED;
import static pl.sparkbit.security.challenge.exception.NoValidTokenFoundException.FailureReason.TOKEN_NOT_FOUND;

@ConditionalOnProperty(value = "sparkbit.security.passwordReset.enabled", havingValue = "true")
@RequiredArgsConstructor
@Service
@Slf4j
@SuppressWarnings("unused")
public class PasswordResetServiceImpl implements PasswordResetService {

    private final IdGenerator idGenerator;
    private final SecurityChallengeTokenGenerator securityChallengeTokenGenerator;
    private final Clock clock;
    private final SecurityChallengeDao securityChallengeDao;
    private final PasswordResetChallengeCallback callback;

    @Value("${sparkbit.security.emailVerification.challengeValidityHours:1}")
    private int challengeValidityHours;

    @Override
    public void initiatePasswordReset(String email) {
        String id = idGenerator.generate();
        String userId = callback.getUserIdForEmail(email);
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
    public void resetPassword(String token, String email) {
        SecurityChallenge challenge = securityChallengeDao.selectChallengeByTokenAndType(token, PASSWORD_RESET);
        if (challenge == null) {
            log.debug("Security challenge with token {} not found", token);
            throw new NoValidTokenFoundException("Valid token not found", TOKEN_NOT_FOUND);
        }

        if (challenge.getExpirationTimestamp().isBefore(clock.instant())) {
            log.debug("Security challenge with token {} expired on {}", token,
                    challenge.getExpirationTimestamp().toEpochMilli());
            throw new NoValidTokenFoundException("Valid token not found", TOKEN_EXPIRED);
        }

        securityChallengeDao.deleteChallenge(challenge.getId());
        log.debug("Password for user {} changed", challenge.getUserId());

        callback.notifyOfSuccess(challenge);
    }
}

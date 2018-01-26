package pl.sparkbit.security.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sparkbit.commons.util.IdGenerator;
import pl.sparkbit.security.callbacks.EmailVerificationChallengeCallback;
import pl.sparkbit.security.dao.SecurityChallengeDao;
import pl.sparkbit.security.domain.SecurityChallenge;
import pl.sparkbit.security.exception.NoValidTokenFoundException;
import pl.sparkbit.security.service.EmailVerificationService;
import pl.sparkbit.security.util.SecurityChallengeTokenGenerator;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static pl.sparkbit.security.config.Properties.EMAIL_VERIFICATION_CHALLENGE_VALIDITY_HOURS;
import static pl.sparkbit.security.config.Properties.EMAIL_VERIFICATION_ENABLED;
import static pl.sparkbit.security.domain.SecurityChallengeType.EMAIL_VERIFICATION;
import static pl.sparkbit.security.exception.NoValidTokenFoundException.FailureReason.TOKEN_EXPIRED;
import static pl.sparkbit.security.exception.NoValidTokenFoundException.FailureReason.TOKEN_NOT_FOUND;

@ConditionalOnProperty(value = EMAIL_VERIFICATION_ENABLED, havingValue = "true")
@RequiredArgsConstructor
@Service
@Slf4j
@SuppressWarnings("unused")
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final IdGenerator idGenerator;
    private final SecurityChallengeTokenGenerator securityChallengeTokenGenerator;
    private final Clock clock;
    private final SecurityChallengeDao securityChallengeDao;
    private final EmailVerificationChallengeCallback callback;

    @Value("${" + EMAIL_VERIFICATION_CHALLENGE_VALIDITY_HOURS + ":1}")
    private int challengeValidityHours;

    @Override
    @Transactional
    public void initiateEmailVerification(String userId) {
        String id = idGenerator.generate();
        String token = securityChallengeTokenGenerator.generateChallengeToken();
        Instant expirationTimestamp = clock.instant().plus(challengeValidityHours, ChronoUnit.HOURS);

        SecurityChallenge challenge = SecurityChallenge.builder()
                .id(id)
                .userId(userId)
                .type(EMAIL_VERIFICATION)
                .token(token)
                .expirationTimestamp(expirationTimestamp)
                .build();

        securityChallengeDao.deleteChallenge(userId, EMAIL_VERIFICATION);
        securityChallengeDao.insertChallenge(challenge);
        log.debug("User {} initiated email verification", userId);

        callback.transmitToUser(challenge);
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        Optional<SecurityChallenge> challengeOpt =
                securityChallengeDao.selectChallengeByTokenAndType(token, EMAIL_VERIFICATION);
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
        log.debug("Email for user {} verified", challenge.getUserId());

        callback.notifyOfSuccess(challenge);
    }
}

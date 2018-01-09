package pl.sparkbit.security.challenge.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sparkbit.commons.util.IdGenerator;
import pl.sparkbit.security.challenge.dao.SecurityChallengeDao;
import pl.sparkbit.security.challenge.domain.SecurityChallenge;
import pl.sparkbit.security.challenge.exception.NoValidTokenFoundException;
import pl.sparkbit.security.challenge.notifiers.EmailVerificationSuccessNotifier;
import pl.sparkbit.security.challenge.service.EmailVerificationService;
import pl.sparkbit.security.challenge.transmitters.EmailVerificationChallengeTransmitter;
import pl.sparkbit.security.challenge.util.SecurityChallengeTokenGenerator;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static pl.sparkbit.security.challenge.domain.SecurityChallengeType.EMAIL_VERIFICATION;
import static pl.sparkbit.security.challenge.exception.NoValidTokenFoundException.FailureReason.TOKEN_EXPIRED;
import static pl.sparkbit.security.challenge.exception.NoValidTokenFoundException.FailureReason.TOKEN_NOT_FOUND;

@ConditionalOnProperty(value = "sparkbit.security.emailVerification.enabled", havingValue = "true")
@RequiredArgsConstructor
@Service
@Slf4j
@SuppressWarnings("unused")
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final IdGenerator idGenerator;
    private final SecurityChallengeTokenGenerator securityChallengeTokenGenerator;
    private final Clock clock;
    private final SecurityChallengeDao securityChallengeDao;
    private final EmailVerificationChallengeTransmitter emailVerificationChallengeTransmitter;
    private final EmailVerificationSuccessNotifier emailVerificationSuccessNotifier;

    @Value("${sparkbit.security.emailVerification.challengeValidityHours:1}")
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

        emailVerificationChallengeTransmitter.transmit(challenge);
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        SecurityChallenge challenge = securityChallengeDao.selectChallengeByTokenAndType(token, EMAIL_VERIFICATION);
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
        log.debug("Email for user {} verified", challenge.getUserId());

        emailVerificationSuccessNotifier.notify(challenge);
    }
}

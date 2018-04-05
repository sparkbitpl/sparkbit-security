package pl.sparkbit.security.util;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.sparkbit.commons.util.IdGenerator;
import pl.sparkbit.security.dao.SecurityChallengeDao;
import pl.sparkbit.security.domain.SecurityChallenge;
import pl.sparkbit.security.domain.SecurityChallengeType;
import pl.sparkbit.security.exception.NoValidTokenFoundException;

import javax.annotation.PostConstruct;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

import static pl.sparkbit.security.config.Properties.*;
import static pl.sparkbit.security.exception.NoValidTokenFoundException.FailureReason.TOKEN_EXPIRED;
import static pl.sparkbit.security.exception.NoValidTokenFoundException.FailureReason.TOKEN_NOT_FOUND;
import static pl.sparkbit.security.util.SecureRandomStringGeneratorImpl.BASE_58_CHARACTERS;

@Component
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class SecurityChallengesImpl implements SecurityChallenges {

    private static final int DEFAULT_CHALLENGE_VALIDITY_HOURS = 1;
    private static final int DEFAULT_CHALLENGE_TOKEN_LENGTH = 6;

    private final IdGenerator idGenerator;
    private final Clock clock;
    private final SecurityChallengeDao securityChallengeDao;
    private final SecureRandomStringGenerator secureRandomStringGenerator;

    private Map<SecurityChallengeType, Integer> validityTimes;

    @Value("${" + CHALLENGE_TOKEN_LENGTH + ":" + DEFAULT_CHALLENGE_TOKEN_LENGTH + "}")
    private int tokenLength;
    @Value("${" + CHALLENGE_TOKEN_ALLOWED_CHARACTERS + ":" + BASE_58_CHARACTERS + "}")
    private String allowedCharacters;

    @Value("${" + PASSWORD_RESET_CHALLENGE_VALIDITY_HOURS + ":" + DEFAULT_CHALLENGE_VALIDITY_HOURS + "}")
    private int passwordResetChallengeValidityHours;
    @Value("${" + EMAIL_VERIFICATION_CHALLENGE_VALIDITY_HOURS + ":" + DEFAULT_CHALLENGE_VALIDITY_HOURS + "}")
    private int emailVerificationChallengeValidityHours;
    @Value("${" + EXTRA_AUTHENTICATION_CHECK_CHALLENGE_VALIDITY_HOURS + ":" + DEFAULT_CHALLENGE_VALIDITY_HOURS + "}")
    private int extraAuthnChallengeValidityHours;

    @PostConstruct
    public void setup() {
        validityTimes = ImmutableMap.of(
                SecurityChallengeType.PASSWORD_RESET, passwordResetChallengeValidityHours,
                SecurityChallengeType.SET_NEW_PASSWORD, passwordResetChallengeValidityHours,
                SecurityChallengeType.EMAIL_VERIFICATION, emailVerificationChallengeValidityHours,
                SecurityChallengeType.EXTRA_AUTHN_CHECK, extraAuthnChallengeValidityHours
        );
    }

    @Override
    public SecurityChallenge createAndInsertChallenge(String userId, SecurityChallengeType type) {
        String id = idGenerator.generate();
        String token = generateChallengeToken();
        Instant expirationTimestamp = clock.instant().plus(validityTimes.get(type), ChronoUnit.HOURS);

        SecurityChallenge challenge = SecurityChallenge.builder()
                .id(id)
                .userId(userId)
                .type(type)
                .token(token)
                .expirationTimestamp(expirationTimestamp)
                .build();

        securityChallengeDao.deleteChallenge(userId, type);
        securityChallengeDao.insertChallenge(challenge);
        return challenge;
    }

    @Override
    public SecurityChallenge finishChallenge(String token, SecurityChallengeType type) {
        Optional<SecurityChallenge> challengeOpt = securityChallengeDao.selectChallengeByTokenAndType(token, type);
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
        return challenge;
    }

    private String generateChallengeToken() {
        return secureRandomStringGenerator.randomString(tokenLength, allowedCharacters);
    }
}

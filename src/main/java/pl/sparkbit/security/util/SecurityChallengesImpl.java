package pl.sparkbit.security.util;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.sparkbit.commons.util.IdGenerator;
import pl.sparkbit.security.config.Properties;
import pl.sparkbit.security.dao.SecurityChallengeDao;
import pl.sparkbit.security.domain.SecurityChallenge;
import pl.sparkbit.security.domain.SecurityChallengeType;
import pl.sparkbit.security.exception.NoValidTokenFoundException;

import javax.annotation.PostConstruct;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static pl.sparkbit.security.exception.NoValidTokenFoundException.FailureReason.TOKEN_EXPIRED;
import static pl.sparkbit.security.exception.NoValidTokenFoundException.FailureReason.TOKEN_NOT_FOUND;

@Component
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class SecurityChallengesImpl implements SecurityChallenges {


    private final IdGenerator idGenerator;
    private final Clock clock;
    private final SecurityChallengeDao securityChallengeDao;
    private final SecureRandomStringGenerator secureRandomStringGenerator;
    private final Properties configuration;

    private Map<SecurityChallengeType, Duration> validityTimes;

    @PostConstruct
    public void setup() {
        validityTimes = ImmutableMap.of(
                SecurityChallengeType.PASSWORD_RESET,
                configuration.getPasswordReset().getChallengeValidity(),
                SecurityChallengeType.SET_NEW_PASSWORD,
                configuration.getPasswordReset().getChallengeValidity(),
                SecurityChallengeType.EMAIL_VERIFICATION,
                configuration.getEmailVerification().getChallengeValidity(),
                SecurityChallengeType.EXTRA_AUTHN_CHECK,
                configuration.getExtraAuthnCheck().getChallengeValidity()
        );
    }

    @Override
    public SecurityChallenge createAndInsertChallenge(String userId, SecurityChallengeType type) {
        String id = idGenerator.generate();
        String token = generateChallengeToken();
        Instant expirationTimestamp = clock.instant().plus(validityTimes.get(type));

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
        return secureRandomStringGenerator.randomString(
                configuration.getChallengeToken().getLength(),
                configuration.getChallengeToken().getAllowedCharacters());
    }
}

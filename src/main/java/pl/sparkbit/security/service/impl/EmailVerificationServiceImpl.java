package pl.sparkbit.security.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sparkbit.security.callbacks.EmailVerificationChallengeCallback;
import pl.sparkbit.security.domain.SecurityChallenge;
import pl.sparkbit.security.mvc.dto.in.VerifyEmailDTO;
import pl.sparkbit.security.service.EmailVerificationService;
import pl.sparkbit.security.util.SecurityChallenges;

import static pl.sparkbit.security.config.Properties.EMAIL_VERIFICATION_ENABLED;
import static pl.sparkbit.security.domain.SecurityChallengeType.EMAIL_VERIFICATION;

@ConditionalOnProperty(value = EMAIL_VERIFICATION_ENABLED, havingValue = "true")
@RequiredArgsConstructor
@Service
@Slf4j
@SuppressWarnings("unused")
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final EmailVerificationChallengeCallback callback;
    private final SecurityChallenges securityChallenges;

    @Override
    @Transactional
    public void initiateEmailVerification(String userId) {
        SecurityChallenge securityChallenge = securityChallenges.createAndInsertChallenge(userId, EMAIL_VERIFICATION);
        callback.transmitToUser(securityChallenge);
        log.debug("User {} initiated email verification", userId);
    }

    @Override
    @Transactional
    public void verifyEmail(VerifyEmailDTO dto) {
        String token = dto.getToken();
        SecurityChallenge challenge = securityChallenges.finishChallenge(token, EMAIL_VERIFICATION);
        callback.notifyOfSuccess(challenge);

        log.debug("Email for user {} verified", challenge.getUserId());
    }
}

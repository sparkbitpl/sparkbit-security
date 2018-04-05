package pl.sparkbit.security.service;

import pl.sparkbit.security.domain.SecurityChallengeType;

import java.util.Map;

public interface PasswordResetService {

    void initiatePasswordReset(Map<String, String> authnAttributesMap);

    void initiateSetNewPassword(String userId);

    void resetPassword(String token, String password, SecurityChallengeType resetType);
}

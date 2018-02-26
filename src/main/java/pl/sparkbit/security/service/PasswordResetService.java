package pl.sparkbit.security.service;

import pl.sparkbit.security.domain.SecurityChallengeType;
import pl.sparkbit.security.login.AuthnAttributes;

public interface PasswordResetService {

    void initiatePasswordReset(AuthnAttributes authnAttributes);

    void initiateSetNewPassword(String userId);

    void resetPassword(String token, String password, SecurityChallengeType resetType);
}

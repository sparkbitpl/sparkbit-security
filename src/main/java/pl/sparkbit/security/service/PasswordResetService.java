package pl.sparkbit.security.service;

import pl.sparkbit.security.login.AuthnAttributes;

public interface PasswordResetService {

    void initiatePasswordReset(AuthnAttributes authnAttributes);

    void resetPassword(String token, String password);
}

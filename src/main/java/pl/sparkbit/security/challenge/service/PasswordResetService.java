package pl.sparkbit.security.challenge.service;

public interface PasswordResetService {

    void initiatePasswordReset(String email);

    void resetPassword(String token, String email);
}

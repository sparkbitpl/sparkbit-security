package pl.sparkbit.security.service;

public interface PasswordResetService {

    void initiatePasswordReset(String email);

    void resetPassword(String token, String password);
}

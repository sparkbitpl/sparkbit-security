package pl.sparkbit.security.service;

public interface EmailVerificationService {

    @SuppressWarnings("unused")
    void initiateEmailVerification(String userId);

    void verifyEmail(String token);
}

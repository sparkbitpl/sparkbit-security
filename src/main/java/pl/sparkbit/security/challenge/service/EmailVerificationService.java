package pl.sparkbit.security.challenge.service;

@SuppressWarnings("unused")
public interface EmailVerificationService {

    void initiateEmailVerification(String userId);

    void verifyEmail(String token);
}

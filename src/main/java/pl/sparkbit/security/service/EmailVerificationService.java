package pl.sparkbit.security.service;

import pl.sparkbit.security.mvc.dto.in.VerifyEmailDTO;

public interface EmailVerificationService {

    @SuppressWarnings("unused")
    void initiateEmailVerification(String userId);

    void verifyEmail(VerifyEmailDTO dto);
}

package pl.sparkbit.security.challenge.callbacks;

public interface PasswordResetChallengeCallback extends BaseChallengeCallback {

    String getUserIdForEmail(String email);
}

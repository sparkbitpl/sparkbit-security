package pl.sparkbit.security.callbacks;

public interface PasswordResetChallengeCallback extends BaseChallengeCallback {

    String getUserIdForEmail(String email);
}

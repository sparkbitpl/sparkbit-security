package pl.sparkbit.security.callbacks;

import java.util.Optional;

public interface PasswordResetChallengeCallback extends BaseChallengeCallback {

    /**
     * It is a good security practice to not disclose if the user with given email was found in the database.
     * If the application chooses to follow this practice it should return Optional.empty when user is not found.
     * If however the application wishes to inform the user that entered email is not found (not recommended) it
     * should throw NotFoundException.
     */
    Optional<String> getUserIdForEmail(String email);
}

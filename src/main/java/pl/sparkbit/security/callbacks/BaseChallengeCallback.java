package pl.sparkbit.security.callbacks;

import pl.sparkbit.security.domain.SecurityChallenge;

public interface BaseChallengeCallback {

    void transmitToUser(SecurityChallenge challenge);

    void notifyOfSuccess(SecurityChallenge challenge);
}

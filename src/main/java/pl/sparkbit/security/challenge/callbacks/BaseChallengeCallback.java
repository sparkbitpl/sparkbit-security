package pl.sparkbit.security.challenge.callbacks;

import pl.sparkbit.security.challenge.domain.SecurityChallenge;

public interface BaseChallengeCallback {

    void transmitToUser(SecurityChallenge challenge);

    void notifyOfSuccess(SecurityChallenge challenge);
}

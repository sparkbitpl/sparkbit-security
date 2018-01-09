package pl.sparkbit.security.challenge.transmitters;

import pl.sparkbit.security.challenge.domain.SecurityChallenge;

public interface EmailVerificationChallengeTransmitter {

    void transmit(SecurityChallenge securityChallenge);
}

package pl.sparkbit.security.challenge.notifiers;

import pl.sparkbit.security.challenge.domain.SecurityChallenge;

public interface EmailVerificationSuccessNotifier {

    void notify(SecurityChallenge challenge);
}

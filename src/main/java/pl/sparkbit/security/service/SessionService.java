package pl.sparkbit.security.service;

import pl.sparkbit.security.domain.NewSessionData;

import java.time.Instant;

public interface SessionService {

    NewSessionData startNewSession(String oldAuthToken);

    void endSession();

    void endAllSessionsForUser(String userId);

    Instant updateAndGetSessionExpirationTimestamp(String authToken);

    boolean isSessionExpirationEnabled();
}

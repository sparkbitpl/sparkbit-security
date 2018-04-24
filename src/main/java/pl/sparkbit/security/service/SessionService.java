package pl.sparkbit.security.service;

import java.time.Instant;

public interface SessionService {

    String startNewSession(String oldAuthToken);

    void endSession();

    void endAllSessionsForUser(String userId);

    Instant updateAndGetSessionExpirationTimestamp(String authToken);

    boolean isSessionExpirationEnabled();
}

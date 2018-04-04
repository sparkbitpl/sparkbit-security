package pl.sparkbit.security.service;

import pl.sparkbit.security.domain.Session;

public interface SessionService {

    String SESSION_EXPIRATION_TIMESTAMP_REQUEST_ATTRIBUTE = "sparkbit.sessionExpirationTimestamp";

    Session startNewSession(String oldAuthToken);

    void endSession();

    void endAllSessionsForUser(String userId);

    void updateSessionExpirationTimestamp(String authToken);

    boolean isSessionExpirationEnabled();

}

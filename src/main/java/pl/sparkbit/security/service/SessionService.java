package pl.sparkbit.security.service;

public interface SessionService {

    String SESSION_EXPIRATION_TIMESTAMP_REQUEST_ATTRIBUTE = "sparkbit.sessionExpirationTimestamp";

    String startNewSession(String oldAuthToken);

    void endSession();

    void endAllSessionsForUser(String userId);

    void updateSessionExpirationTimestamp(String authToken);

    boolean isSessionExpirationEnabled();
}

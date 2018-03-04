package pl.sparkbit.security.service;

import pl.sparkbit.security.domain.Session;

public interface SessionService {

    String SESSION_EXPIRATION_TS_REQUEST_ATTRIBUTE = "sparkbit.sessionExpirationTs";

    Session startNewSession(String oldAuthToken);

    void endSession();

    void endAllSessionsForUser(String userId);

    void updateSessionExpiryTs(String authToken);

    boolean areSessionsImmortal();

}

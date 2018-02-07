package pl.sparkbit.security.service;

import pl.sparkbit.security.domain.Session;

public interface SessionService {

    Session startNewSession(String oldAuthToken);

    void endSession();

    void endAllSessionsForUser(String userId);

    void updateExpirationTime(String authToken);

    boolean areSessionsImmortal();

}

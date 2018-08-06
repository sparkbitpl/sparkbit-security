package pl.sparkbit.security.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sparkbit.security.dao.CredentialsDao;
import pl.sparkbit.security.dao.SessionDao;
import pl.sparkbit.security.domain.Credentials;
import pl.sparkbit.security.service.CredentialsService;

import java.time.Clock;
import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
@Service
@SuppressWarnings("unused")
public class CredentialsServiceImpl implements CredentialsService {

    private final SessionDao sessionDao;
    private final CredentialsDao credentialsDao;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    @Override
    @Transactional
    public void insertCredentials(Credentials credentials, GrantedAuthority role) {
        insertCredentials(credentials, Collections.singleton(role));
    }

    @Override
    @Transactional
    public void insertCredentials(Credentials credentials, Collection<GrantedAuthority> roles) {
        String encodedPassword = passwordEncoder.encode(credentials.getPassword());
        Credentials encodedCredentials = credentials.toBuilder().password(encodedPassword).build();
        credentialsDao.insertCredentials(encodedCredentials);
        for (GrantedAuthority role : roles) {
            credentialsDao.insertUserRole(credentials.getUserId(), role);
        }
    }

    @Override
    @Transactional
    public void disableUser(String userId) {
        Credentials credentials = Credentials.builder().userId(userId).enabled(false).build();
        credentialsDao.updateCredentials(credentials);
        sessionDao.deleteSessions(userId, clock.instant());
    }

    @Override
    @Transactional
    public void enableUser(String userId) {
        Credentials credentials = Credentials.builder().userId(userId).enabled(true).build();
        credentialsDao.updateCredentials(credentials);
    }

    @Override
    @Transactional
    public void deleteUser(String userId) {
        Credentials credentials = Credentials.builder().userId(userId).deleted(true).build();
        credentialsDao.updateCredentials(credentials);
        sessionDao.deleteSessions(userId, clock.instant());
    }
}

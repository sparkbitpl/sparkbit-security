package pl.sparkbit.security.rest.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sparkbit.security.Security;
import pl.sparkbit.security.rest.dao.RestSecurityDao;
import pl.sparkbit.security.rest.domain.Credentials;
import pl.sparkbit.security.rest.domain.RestUserDetails;
import pl.sparkbit.security.rest.exception.InvalidCurrentPasswordException;
import pl.sparkbit.security.rest.exception.SessionNotFoundException;
import pl.sparkbit.security.rest.mvc.dto.in.ChangePasswordDTO;
import pl.sparkbit.security.rest.service.RestSecurityService;
import pl.sparkbit.security.session.dao.SessionDao;

import java.time.Clock;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@SuppressWarnings("unused")
public class RestSecurityServiceImpl implements RestSecurityService {

    private final RestSecurityDao restSecurityDao;
    private final SessionDao sessionDao;
    private final PasswordEncoder passwordEncoder;
    private final Security security;
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
        restSecurityDao.insertCredentials(encodedCredentials);
        for (GrantedAuthority role : roles) {
            restSecurityDao.insertUserRole(credentials.getUserId(), role);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RestUserDetails retrieveRestUserDetails(String authToken) {
        Optional<RestUserDetails> restUserDetails = restSecurityDao.selectRestUserDetails(authToken);
        return restUserDetails.orElseThrow(() -> new SessionNotFoundException("Session not found"));
    }

    @Override
    @Transactional
    public void changeCurrentUserPassword(ChangePasswordDTO dto) {
        String userId = security.currentUserId();

        verifyCurrentPassword(userId, dto.getCurrentPassword());

        String encodedPassword = passwordEncoder.encode(dto.getNewPassword());
        Credentials credentials = Credentials.builder().userId(userId).password(encodedPassword).build();
        restSecurityDao.updateCredentials(credentials);
    }

    private void verifyCurrentPassword(String userId, String receivedCurrentPassword) {
        String storedPassword = restSecurityDao.selectPasswordHashForUser(userId);
        boolean matches = passwordEncoder.matches(receivedCurrentPassword, storedPassword);
        if (!matches) {
            throw new InvalidCurrentPasswordException("Invalid current password");
        }
    }

    @Override
    public void disableUser(String userId) {
        Credentials credentials = Credentials.builder().userId(userId).enabled(false).build();
        restSecurityDao.updateCredentials(credentials);
        sessionDao.deleteSessions(userId, clock.instant());
    }

    @Override
    public void enableUser(String userId) {
        Credentials credentials = Credentials.builder().userId(userId).enabled(true).build();
        restSecurityDao.updateCredentials(credentials);
    }

    @Override
    public void deleteUser(String userId) {
        Credentials credentials = Credentials.builder().userId(userId).deleted(true).build();
        restSecurityDao.updateCredentials(credentials);
        sessionDao.deleteSessions(userId, clock.instant());
    }
}

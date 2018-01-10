package pl.sparkbit.security.rest.service.impl;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sparkbit.security.Security;
import pl.sparkbit.security.rest.dao.RestSecurityDao;
import pl.sparkbit.security.rest.domain.RestUserDetails;
import pl.sparkbit.security.rest.exception.InvalidCurrentPasswordException;
import pl.sparkbit.security.rest.exception.SessionNotFoundException;
import pl.sparkbit.security.rest.mvc.dto.in.ChangePasswordDTO;
import pl.sparkbit.security.rest.service.RestSecurityService;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@SuppressWarnings("unused")
public class RestSecurityServiceImpl implements RestSecurityService {

    private final RestSecurityDao restSecurityDao;
    private final PasswordEncoder passwordEncoder;
    private final Security security;

    @Value("${sparkbit.security.expected-authn-attributes}")
    private String authnAttribute;

    /**
     * Use this method only for applications with a single authn attribute (eg. email or username)
     */
    @Override
    @Transactional
    public void insertCredentialsAndRoles(String userId, String username, String password, boolean enabled,
                                          boolean deleted, Collection<String> roles) {
        if (authnAttribute.contains(",")) {
            throw new IllegalArgumentException("Method only allowed for applications with single authn attribute");
        }
        insertCredentialsAndRoles(userId, password, enabled, deleted, ImmutableMap.of(authnAttribute, username), roles);
    }

    /**
     * Use this method for applications with a multiple authn attribute (eg. email and application name)
     */
    @Override
    @Transactional
    public void insertCredentialsAndRoles(String userId, String password, boolean enabled, boolean deleted,
                                          Map<String, String> authnAttributes, Collection<String> roles) {
        String encodedPassword = passwordEncoder.encode(password);
        restSecurityDao.insertCredentials(userId, encodedPassword, enabled, deleted, authnAttributes);
        for (String role : roles) {
            restSecurityDao.insertUserRole(userId, role);
        }
    }

    @Override
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
        restSecurityDao.updatePassword(userId, encodedPassword);
    }

    private void verifyCurrentPassword(String userId, String receivedCurrentPassword) {
        String storedPassword = restSecurityDao.selectPasswordHashForUser(userId);
        boolean matches = passwordEncoder.matches(receivedCurrentPassword, storedPassword);
        if (!matches) {
            throw new InvalidCurrentPasswordException("Invalid current password");
        }
    }
}

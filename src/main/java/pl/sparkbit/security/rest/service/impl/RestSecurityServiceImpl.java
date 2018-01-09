package pl.sparkbit.security.rest.service.impl;

import lombok.RequiredArgsConstructor;
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

import java.util.Optional;

@RequiredArgsConstructor
@Service
@SuppressWarnings("unused")
public class RestSecurityServiceImpl implements RestSecurityService {

    private final RestSecurityDao restSecurityDao;
    private final PasswordEncoder passwordEncoder;
    private final Security security;

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

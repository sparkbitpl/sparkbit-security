package pl.sparkbit.security.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sparkbit.security.Security;
import pl.sparkbit.security.dao.CredentialsDao;
import pl.sparkbit.security.domain.Credentials;
import pl.sparkbit.security.exception.InvalidCurrentPasswordException;
import pl.sparkbit.security.mvc.dto.in.ChangePasswordDTO;
import pl.sparkbit.security.service.PasswordChangeService;

import static pl.sparkbit.security.config.Properties.PASSWORD_CHANGE_ENABLED;

@ConditionalOnProperty(value = PASSWORD_CHANGE_ENABLED, havingValue = "true")
@RequiredArgsConstructor
@Service
@Slf4j
@SuppressWarnings("unused")
public class PasswordChangeServiceImpl implements PasswordChangeService {

    private final CredentialsDao credentialsDao;
    private final PasswordEncoder passwordEncoder;
    private final Security security;

    @Override
    @Transactional
    public void changeCurrentUserPassword(ChangePasswordDTO dto) {
        String userId = security.currentUserId();

        verifyCurrentPassword(userId, dto.getCurrentPassword());

        String encodedPassword = passwordEncoder.encode(dto.getNewPassword());
        Credentials credentials = Credentials.builder().userId(userId).password(encodedPassword).build();
        credentialsDao.updateCredentials(credentials);
    }

    private void verifyCurrentPassword(String userId, String receivedCurrentPassword) {
        //noinspection ConstantConditions - will never be empty
        String storedPassword = credentialsDao.selectPasswordHashForUser(userId).get();
        boolean matches = passwordEncoder.matches(receivedCurrentPassword, storedPassword);
        if (!matches) {
            throw new InvalidCurrentPasswordException("Invalid current password");
        }
    }
}

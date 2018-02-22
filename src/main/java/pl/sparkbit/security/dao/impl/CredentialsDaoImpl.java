package pl.sparkbit.security.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.sparkbit.security.dao.CredentialsDao;
import pl.sparkbit.security.dao.mybatis.CredentialsMapper;
import pl.sparkbit.security.domain.Credentials;

import java.util.Optional;

import static org.springframework.transaction.annotation.Propagation.MANDATORY;
import static pl.sparkbit.security.config.Properties.USER_ENTITY_NAME;

@Repository
@RequiredArgsConstructor
@SuppressWarnings("unused")
@Transactional(propagation = MANDATORY)
public class CredentialsDaoImpl implements CredentialsDao {

    private final CredentialsMapper credentialsMapper;

    @Value("${" + USER_ENTITY_NAME + ":user}")
    private String prefix;

    @Override
    public void insertCredentials(Credentials credentials) {
        credentialsMapper.insertCredentials(credentials, prefix);
    }

    @Override
    public void insertUserRole(String userId, GrantedAuthority role) {
        credentialsMapper.insertUserRole(userId, role, prefix);
    }

    @Override
    public Optional<String> selectPasswordHashForUser(String userId) {
        return Optional.ofNullable(credentialsMapper.selectPasswordHashForUser(userId, prefix));
    }

    @Override
    public void updateCredentials(Credentials credentials) {
        credentialsMapper.updateCredentials(credentials, prefix);
    }
}

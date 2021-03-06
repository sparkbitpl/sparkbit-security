package pl.sparkbit.security.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Transactional;
import pl.sparkbit.security.config.SecurityProperties;
import pl.sparkbit.security.dao.CredentialsDao;
import pl.sparkbit.security.dao.mybatis.CredentialsMapper;
import pl.sparkbit.security.domain.Credentials;

import java.util.Optional;

import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@RequiredArgsConstructor
@SuppressWarnings("unused")
@Transactional(propagation = MANDATORY)
public class CredentialsDaoImpl implements CredentialsDao {

    private final CredentialsMapper credentialsMapper;
    private final SecurityProperties configuration;

    @Override
    public void insertCredentials(Credentials credentials) {
        credentialsMapper.insertCredentials(credentials, configuration.getDatabaseSchema().getUserEntityName());
    }

    @Override
    public void insertUserRole(String userId, GrantedAuthority role) {
        credentialsMapper.insertUserRole(userId, role, configuration.getDatabaseSchema().getUserEntityName());
    }

    @Override
    public Optional<String> selectPasswordHashForUser(String userId) {
        return Optional.ofNullable(credentialsMapper.selectPasswordHashForUser(userId,
                configuration.getDatabaseSchema().getUserEntityName()));
    }

    @Override
    public void updateCredentials(Credentials credentials) {
        credentialsMapper.updateCredentials(credentials, configuration.getDatabaseSchema().getUserEntityName());
    }
}

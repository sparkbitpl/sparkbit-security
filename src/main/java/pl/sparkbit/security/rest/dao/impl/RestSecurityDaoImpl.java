package pl.sparkbit.security.rest.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.sparkbit.security.rest.dao.RestSecurityDao;
import pl.sparkbit.security.rest.dao.mybatis.RestSecurityMapper;
import pl.sparkbit.security.rest.domain.Credentials;
import pl.sparkbit.security.rest.domain.RestUserDetails;

import java.util.Optional;

import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@Repository
@RequiredArgsConstructor
@SuppressWarnings("unused")
@Transactional(propagation = MANDATORY)
public class RestSecurityDaoImpl implements RestSecurityDao {

    private final RestSecurityMapper restSecurityMapper;

    @Value("${sparkbit.security.user-entity-name:user}")
    private String prefix;

    @Override
    public void insertCredentials(Credentials credentials) {
        restSecurityMapper.insertCredentials(credentials, prefix);
    }

    @Override
    public void insertUserRole(String userId, GrantedAuthority role) {
        restSecurityMapper.insertUserRole(userId, role);
    }

    @Override
    public Optional<RestUserDetails> selectRestUserDetails(String authToken) {
        return Optional.ofNullable(restSecurityMapper.selectRestUserDetails(authToken, prefix));
    }

    @Override
    public String selectPasswordHashForUser(String userId) {
        return restSecurityMapper.selectPasswordHashForUser(userId, prefix);
    }

    @Override
    public void updateCredentials(Credentials credentials) {
        restSecurityMapper.updateCredentials(credentials, prefix);
    }
}

package pl.sparkbit.security.rest.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import pl.sparkbit.security.rest.dao.RestSecurityDao;
import pl.sparkbit.security.rest.dao.mybatis.RestSecurityMapper;
import pl.sparkbit.security.rest.domain.RestUserDetails;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class RestSecurityDaoImpl implements RestSecurityDao {

    private final RestSecurityMapper restSecurityMapper;

    @Value("${sparkbit.security.user-entity-name:user}")
    private String prefix;

    @Override
    public Optional<RestUserDetails> selectRestUserDetails(String authToken) {
        return Optional.ofNullable(restSecurityMapper.selectRestUserDetails(authToken, prefix));
    }

    @Override
    public String selectPasswordHashForUser(String userId) {
        return restSecurityMapper.selectPasswordHashForUser(userId, prefix);
    }

    @Override
    public void updatePassword(String userId, String password) {
        restSecurityMapper.updatePassword(userId, password, prefix);
    }
}

package pl.sparkbit.security.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import pl.sparkbit.security.dao.SecurityDao;
import pl.sparkbit.security.dao.mybatis.SecurityMapper;
import pl.sparkbit.security.domain.Session;
import pl.sparkbit.security.login.AuthnAttributes;
import pl.sparkbit.security.login.LoginUserDetails;
import pl.sparkbit.security.rest.RestUserDetails;

import java.time.Instant;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class SecurityDaoImpl implements SecurityDao {

    private final SecurityMapper securityMapper;

    @Value("${sparkbit.security.user-entity-name:user}")
    private String prefix;

    @Override
    public void insertSession(Session session) {
        securityMapper.insertSession(session, prefix);
    }

    @Override
    public Optional<LoginUserDetails> selectLoginUserDetails(AuthnAttributes authnAttributes) {
        return Optional.ofNullable(securityMapper.selectLoginUserDetails(authnAttributes, prefix));
    }

    @Override
    public Optional<RestUserDetails> selectRestUserDetails(String authToken) {
        return Optional.ofNullable(securityMapper.selectRestUserDetails(authToken, prefix));
    }

    @Override
    public Optional<Session> selectSession(String authToken) {
        return Optional.ofNullable(securityMapper.selectSession(authToken, prefix));
    }

    @Override
    public void deleteSession(String authToken, Instant deletedTs) {
        securityMapper.deleteSession(authToken, deletedTs, prefix);
    }

    @Override
    public void deleteSessions(Instant olderThan) {
        securityMapper.deleteSessions(olderThan, prefix);
    }
}

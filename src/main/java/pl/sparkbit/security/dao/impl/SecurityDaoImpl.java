package pl.sparkbit.security.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import pl.sparkbit.security.dao.SecurityDao;
import pl.sparkbit.security.dao.mybatis.SecurityMapper;
import pl.sparkbit.security.domain.Session;
import pl.sparkbit.security.login.AuthnAttributes;
import pl.sparkbit.security.login.LoginUserDetails;
import pl.sparkbit.security.rest.RestUserDetails;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class SecurityDaoImpl implements SecurityDao {

    private final SecurityMapper securityMapper;

    @Override
    public void insertSession(Session session) {
        securityMapper.insertSession(session);
    }

    @Override
    public Optional<LoginUserDetails> selectLoginUserDetails(AuthnAttributes authnAttributes) {
        return Optional.ofNullable(securityMapper.selectLoginUserDetails(authnAttributes));
    }

    @Override
    public Optional<RestUserDetails> selectRestUserDetails(String sessionId) {
        return Optional.ofNullable(securityMapper.selectRestUserDetails(sessionId));
    }

    @Override
    public Optional<Session> selectSession(String id) {
        return Optional.ofNullable(securityMapper.selectSession(id));
    }

    @Override
    public void deleteSession(String sessionId) {
        securityMapper.deleteSession(sessionId);
    }
}

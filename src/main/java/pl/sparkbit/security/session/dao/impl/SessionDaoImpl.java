package pl.sparkbit.security.session.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.sparkbit.security.session.auth.AuthnAttributes;
import pl.sparkbit.security.session.auth.LoginUserDetails;
import pl.sparkbit.security.session.dao.SessionDao;
import pl.sparkbit.security.session.dao.mybatis.SessionMapper;
import pl.sparkbit.security.session.domain.Session;

import java.time.Instant;
import java.util.Optional;

import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@Repository
@RequiredArgsConstructor
@SuppressWarnings("unused")
@Transactional(propagation = MANDATORY)
public class SessionDaoImpl implements SessionDao {

    private final SessionMapper sessionMapper;

    @Value("${sparkbit.security.user-entity-name:user}")
    private String prefix;
    @Value("${sparkbit.security.userTableName:uzer}")
    private String userTableName;

    @Override
    public void insertSession(Session session) {
        sessionMapper.insertSession(session, prefix);
    }

    @Override
    public Optional<Session> selectSession(String authToken) {
        return Optional.ofNullable(sessionMapper.selectSession(authToken, prefix));
    }

    @Override
    public Optional<String> selectUserId(AuthnAttributes authnAttributes) {
        return Optional.ofNullable(sessionMapper.selectUserId(userTableName, authnAttributes, prefix));
    }

    @Override
    public Optional<LoginUserDetails> selectLoginUserDetails(String userId) {
        return Optional.ofNullable(sessionMapper.selectLoginUserDetails(userId, prefix));
    }

    @Override
    public void deleteSession(String authToken, Instant deletedTs) {
        sessionMapper.deleteSession(authToken, deletedTs, prefix);
    }

    @Override
    public void deleteSessions(Instant olderThan) {
        sessionMapper.deleteExpiredSessions(olderThan, prefix);
    }

    @Override
    public void deleteSessions(String userId, Instant deletedTs) {
        sessionMapper.deleteSessionsForUser(userId, deletedTs, prefix);
    }
}

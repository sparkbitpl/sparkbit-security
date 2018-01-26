package pl.sparkbit.security.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.sparkbit.security.dao.SessionDao;
import pl.sparkbit.security.dao.mybatis.SessionMapper;
import pl.sparkbit.security.domain.Session;

import java.time.Instant;
import java.util.Optional;

import static org.springframework.transaction.annotation.Propagation.MANDATORY;
import static pl.sparkbit.security.config.Properties.USER_ENTITY_NAME;

@Repository
@RequiredArgsConstructor
@SuppressWarnings("unused")
@Transactional(propagation = MANDATORY)
public class SessionDaoImpl implements SessionDao {

    private final SessionMapper sessionMapper;

    @Value("${" + USER_ENTITY_NAME + ":user}")
    private String prefix;

    @Override
    public void insertSession(Session session) {
        sessionMapper.insertSession(session, prefix);
    }

    @Override
    public Optional<Session> selectSession(String authToken) {
        return Optional.ofNullable(sessionMapper.selectSession(authToken, prefix));
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

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
    public void updateSessionExpirationTimestamp(String authTokenHash, Instant expirationTimestamp) {
        sessionMapper.updateSessionExpirationTimestamp(authTokenHash, expirationTimestamp, prefix);
    }

    @Override
    public void updateExtraAuthnCheckRequired(String authTokenHash, boolean value) {
        sessionMapper.updateExtraAuthnCheckRequired(authTokenHash, value, prefix);
    }

    @Override
    public Optional<Session> selectSession(String authTokenHash) {
        return Optional.ofNullable(sessionMapper.selectSession(authTokenHash, prefix));
    }

    @Override
    public void deleteSession(String authTokenHash, Instant deletionTimestamp) {
        sessionMapper.deleteSession(authTokenHash, deletionTimestamp, prefix);
    }

    @Override
    public void deleteSessions(String userId, Instant deletionTimestamp) {
        sessionMapper.deleteSessions(userId, deletionTimestamp, prefix);
    }

    @Override
    public void deleteExpiredSessions(Instant now) {
        sessionMapper.deleteExpiredSessions(now, prefix);
    }

    @Override
    public void purgeDeletedSessions(Instant deletedBefore) {
        sessionMapper.purgeDeletedSessions(deletedBefore, prefix);
    }
}

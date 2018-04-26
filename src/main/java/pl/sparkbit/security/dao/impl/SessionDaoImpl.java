package pl.sparkbit.security.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.sparkbit.security.config.Properties;
import pl.sparkbit.security.dao.SessionDao;
import pl.sparkbit.security.dao.mybatis.SessionMapper;
import pl.sparkbit.security.domain.Session;

import java.time.Instant;
import java.util.Optional;

import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@Repository
@RequiredArgsConstructor
@SuppressWarnings("unused")
@Transactional(propagation = MANDATORY)
public class SessionDaoImpl implements SessionDao {

    private final SessionMapper sessionMapper;
    private final Properties configuration;

    @Override
    public void insertSession(Session session) {
        sessionMapper.insertSession(session, configuration.getUserEntityName());
    }

    @Override
    public void updateSessionExpirationTimestamp(String authTokenHash, Instant expirationTimestamp) {
        sessionMapper.updateSessionExpirationTimestamp(authTokenHash, expirationTimestamp,
                configuration.getUserEntityName());
    }

    @Override
    public void updateExtraAuthnCheckRequired(String authTokenHash, boolean value) {
        sessionMapper.updateExtraAuthnCheckRequired(authTokenHash, value, configuration.getUserEntityName());
    }

    @Override
    public Optional<Session> selectSession(String authTokenHash) {
        return Optional.ofNullable(sessionMapper.selectSession(authTokenHash, configuration.getUserEntityName()));
    }

    @Override
    public void deleteSession(String authTokenHash, Instant deletionTimestamp) {
        sessionMapper.deleteSession(authTokenHash, deletionTimestamp, configuration.getUserEntityName());
    }

    @Override
    public void deleteSessions(String userId, Instant deletionTimestamp) {
        sessionMapper.deleteSessions(userId, deletionTimestamp, configuration.getUserEntityName());
    }

    @Override
    public void deleteExpiredSessions(Instant now) {
        sessionMapper.deleteExpiredSessions(now, configuration.getUserEntityName());
    }

    @Override
    public void purgeDeletedSessions(Instant deletedBefore) {
        sessionMapper.purgeDeletedSessions(deletedBefore, configuration.getUserEntityName());
    }
}

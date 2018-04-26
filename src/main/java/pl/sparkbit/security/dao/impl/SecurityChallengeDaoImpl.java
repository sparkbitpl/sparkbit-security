package pl.sparkbit.security.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.sparkbit.security.config.Properties;
import pl.sparkbit.security.dao.SecurityChallengeDao;
import pl.sparkbit.security.dao.mybatis.SecurityChallengeMapper;
import pl.sparkbit.security.domain.SecurityChallenge;
import pl.sparkbit.security.domain.SecurityChallengeType;

import java.time.Instant;
import java.util.Optional;

import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@Repository
@RequiredArgsConstructor
@SuppressWarnings("unused")
@Transactional(propagation = MANDATORY)
public class SecurityChallengeDaoImpl implements SecurityChallengeDao {

    private final SecurityChallengeMapper securityChallengeMapper;
    private final Properties configuration;

    @Override
    public void insertChallenge(SecurityChallenge securityChallenge) {
        securityChallengeMapper.insertChallenge(securityChallenge, configuration.getUserEntityName());
    }

    @Override
    public Optional<SecurityChallenge> selectChallengeByTokenAndType(String token, SecurityChallengeType type) {
        return Optional.ofNullable(securityChallengeMapper.selectChallengeByTokenAndType(token, type,
                configuration.getUserEntityName()));
    }

    @Override
    public void deleteChallenge(String id) {
        securityChallengeMapper.deleteChallengeById(id, configuration.getUserEntityName());
    }

    @Override
    public void deleteChallenge(String userId, SecurityChallengeType type) {
        securityChallengeMapper.deleteChallengeByUserIdAndType(userId, type, configuration.getUserEntityName());
    }

    @Override
    public void deleteExpiredChallenges(Instant now) {
        securityChallengeMapper.deleteExpiredChallenges(now, configuration.getUserEntityName());
    }
}

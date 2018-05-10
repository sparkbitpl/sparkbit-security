package pl.sparkbit.security.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.sparkbit.security.config.SecurityProperties;
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
    private final SecurityProperties configuration;

    @Override
    public void insertChallenge(SecurityChallenge securityChallenge) {
        securityChallengeMapper.insertChallenge(securityChallenge,
                configuration.getDatabaseSchema().getUserEntityName());
    }

    @Override
    public Optional<SecurityChallenge> selectChallengeByTokenAndType(String token, SecurityChallengeType type) {
        return Optional.ofNullable(securityChallengeMapper.selectChallengeByTokenAndType(token, type,
                configuration.getDatabaseSchema().getUserEntityName()));
    }

    @Override
    public void deleteChallenge(String id) {
        securityChallengeMapper.deleteChallengeById(id, configuration.getDatabaseSchema().getUserEntityName());
    }

    @Override
    public void deleteChallenge(String userId, SecurityChallengeType type) {
        securityChallengeMapper.deleteChallengeByUserIdAndType(userId,
                type, configuration.getDatabaseSchema().getUserEntityName());
    }

    @Override
    public void deleteExpiredChallenges(Instant now) {
        securityChallengeMapper.deleteExpiredChallenges(now, configuration.getDatabaseSchema().getUserEntityName());
    }
}

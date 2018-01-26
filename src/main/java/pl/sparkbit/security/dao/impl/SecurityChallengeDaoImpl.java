package pl.sparkbit.security.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.sparkbit.security.dao.SecurityChallengeDao;
import pl.sparkbit.security.dao.mybatis.SecurityChallengeMapper;
import pl.sparkbit.security.domain.SecurityChallenge;
import pl.sparkbit.security.domain.SecurityChallengeType;

import java.time.Instant;

import static org.springframework.transaction.annotation.Propagation.MANDATORY;
import static pl.sparkbit.security.Properties.USER_ENTITY_NAME;

@Repository
@RequiredArgsConstructor
@SuppressWarnings("unused")
@Transactional(propagation = MANDATORY)
public class SecurityChallengeDaoImpl implements SecurityChallengeDao {

    private final SecurityChallengeMapper securityChallengeMapper;

    @Value("${" + USER_ENTITY_NAME + ":user}")
    private String prefix;

    @Override
    public void insertChallenge(SecurityChallenge securityChallenge) {
        securityChallengeMapper.insertChallenge(securityChallenge, prefix);
    }

    @Override
    public SecurityChallenge selectChallengeByTokenAndType(String token, SecurityChallengeType type) {
        return securityChallengeMapper.selectChallengeByToken(token, type, prefix);
    }

    @Override
    public void deleteChallenge(String id) {
        securityChallengeMapper.deleteChallengeById(id);
    }

    @Override
    public void deleteChallenge(String userId, SecurityChallengeType type) {
        securityChallengeMapper.deleteChallengeByUserIdAndType(userId, type, prefix);
    }

    @Override
    public void deleteExpiredChallenges(Instant now) {
        securityChallengeMapper.deleteExpiredChallenges(now);
    }
}

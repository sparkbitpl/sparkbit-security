package pl.sparkbit.security.challenge.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import pl.sparkbit.security.challenge.dao.SecurityChallengeDao;
import pl.sparkbit.security.challenge.dao.mybatis.SecurityChallengeMapper;
import pl.sparkbit.security.challenge.domain.SecurityChallenge;
import pl.sparkbit.security.challenge.domain.SecurityChallengeType;

@Repository
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class SecurityChallengeDaoImpl implements SecurityChallengeDao {

    private final SecurityChallengeMapper securityChallengeMapper;

    @Value("${sparkbit.security.user-entity-name:user}")
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
}

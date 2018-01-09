package pl.sparkbit.security.challenge.dao;

import pl.sparkbit.security.challenge.domain.SecurityChallenge;
import pl.sparkbit.security.challenge.domain.SecurityChallengeType;

public interface SecurityChallengeDao {

    void insertChallenge(SecurityChallenge securityChallenge);

    SecurityChallenge selectChallengeByTokenAndType(String token, SecurityChallengeType type);

    void deleteChallenge(String id);

    void deleteChallenge(String userId, SecurityChallengeType type);
}

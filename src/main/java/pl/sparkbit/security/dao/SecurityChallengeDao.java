package pl.sparkbit.security.dao;

import pl.sparkbit.security.domain.SecurityChallenge;
import pl.sparkbit.security.domain.SecurityChallengeType;

import java.time.Instant;

public interface SecurityChallengeDao {

    void insertChallenge(SecurityChallenge securityChallenge);

    SecurityChallenge selectChallengeByTokenAndType(String token, SecurityChallengeType type);

    void deleteChallenge(String id);

    void deleteChallenge(String userId, SecurityChallengeType type);

    void deleteExpiredChallenges(Instant now);
}

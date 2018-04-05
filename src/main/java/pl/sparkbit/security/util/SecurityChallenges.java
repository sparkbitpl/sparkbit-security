package pl.sparkbit.security.util;

import pl.sparkbit.security.domain.SecurityChallenge;
import pl.sparkbit.security.domain.SecurityChallengeType;

public interface SecurityChallenges {

    SecurityChallenge createAndInsertChallenge(String userId, SecurityChallengeType type);

    SecurityChallenge finishChallenge(String token, SecurityChallengeType type);
}

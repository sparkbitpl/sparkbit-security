package pl.sparkbit.security.challenge.dao.mybatis;

import org.apache.ibatis.annotations.Param;
import pl.sparkbit.security.challenge.domain.SecurityChallenge;
import pl.sparkbit.security.challenge.domain.SecurityChallengeType;

import java.time.Instant;

public interface SecurityChallengeMapper {

    void insertChallenge(@Param("challenge") SecurityChallenge challenge, @Param("prefix") String prefix);

    SecurityChallenge selectChallengeByToken(@Param("token") String token, @Param("type") SecurityChallengeType type,
                                             @Param("prefix") String prefix);

    void deleteChallengeById(@Param("id") String id);

    void deleteChallengeByUserIdAndType(@Param("userId") String userId, @Param("type") SecurityChallengeType type,
                                        @Param("prefix") String prefix);

    void deleteExpiredChallenges(@Param("now") Instant now);
}

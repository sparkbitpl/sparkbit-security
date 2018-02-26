package pl.sparkbit.security.dao.mybatis;

import org.apache.ibatis.annotations.Param;
import pl.sparkbit.security.domain.SecurityChallenge;
import pl.sparkbit.security.domain.SecurityChallengeType;

import java.time.Instant;

public interface SecurityChallengeMapper {

    void insertChallenge(@Param("challenge") SecurityChallenge challenge, @Param("prefix") String prefix);

    SecurityChallenge selectChallengeByTokenAndType(@Param("token") String token,
                                                    @Param("type") SecurityChallengeType type,
                                                    @Param("prefix") String prefix);

    void deleteChallengeById(@Param("id") String id, @Param("prefix") String prefix);

    void deleteChallengeByUserIdAndType(@Param("userId") String userId, @Param("type") SecurityChallengeType type,
                                        @Param("prefix") String prefix);

    void deleteExpiredChallenges(@Param("now") Instant now, @Param("prefix") String prefix);
}

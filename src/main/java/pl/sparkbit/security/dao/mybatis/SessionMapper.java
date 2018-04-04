package pl.sparkbit.security.dao.mybatis;

import org.apache.ibatis.annotations.Param;
import pl.sparkbit.security.domain.Session;

import java.time.Instant;

public interface SessionMapper {

    void insertSession(@Param("session") Session session, @Param("prefix") String prefix);

    void updateSessionExpirationTimestamp(@Param("authToken") String authToken,
                                          @Param("expirationTimestamp") Instant expirationTimestamp,
                                          @Param("prefix") String prefix);

    Session selectSession(@Param("authToken") String authToken, @Param("prefix") String prefix);

    void deleteSession(@Param("authToken") String authToken, @Param("deletionTimestamp") Instant deletionTimestamp,
                       @Param("prefix") String prefix);

    void deleteSessions(@Param("userId") String userId, @Param("deletionTimestamp") Instant deletionTimestamp,
                               @Param("prefix") String prefix);

    void deleteExpiredSessions(@Param("now") Instant now, @Param("prefix") String prefix);

    void purgeDeletedSessions(@Param("deletedBefore") Instant deletedBefore, @Param("prefix") String prefix);
}

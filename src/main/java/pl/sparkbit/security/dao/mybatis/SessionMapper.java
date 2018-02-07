package pl.sparkbit.security.dao.mybatis;

import org.apache.ibatis.annotations.Param;
import pl.sparkbit.security.domain.Session;

import java.time.Instant;

public interface SessionMapper {

    void insertSession(@Param("session") Session session, @Param("prefix") String prefix);

    void updateSessionExpiryTs(@Param("authToken") String authToken, @Param("expiresAt") Instant expiresAt,
                               @Param("prefix") String prefix);

    Session selectSession(@Param("authToken") String authToken, @Param("prefix") String prefix);

    void deleteSession(@Param("authToken") String authToken, @Param("deletedTs") Instant deletedTs,
                       @Param("prefix") String prefix);

    void deleteSessionsMarkedAsDeleted(@Param("olderThan") Instant olderThan, @Param("prefix") String prefix);

    void markSessionsAsDeleted(@Param("userId") String userId, @Param("deletedTs") Instant deletedTs,
                               @Param("prefix") String prefix);

    void deleteExpiredSessions(@Param("now") Instant now, @Param("prefix") String prefix);
}

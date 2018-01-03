package pl.sparkbit.security.session.dao.mybatis;

import org.apache.ibatis.annotations.Param;
import pl.sparkbit.security.session.auth.AuthnAttributes;
import pl.sparkbit.security.session.auth.LoginUserDetails;
import pl.sparkbit.security.session.domain.Session;

import java.time.Instant;

public interface SessionMapper {

    void insertSession(@Param("session") Session session, @Param("prefix") String prefix);

    Session selectSession(@Param("authToken") String authToken, @Param("prefix") String prefix);

    LoginUserDetails selectLoginUserDetails(@Param("authnAttributes") AuthnAttributes authnAttributes,
            @Param("prefix") String prefix);

    void deleteSession(@Param("authToken") String authToken, @Param("deletedTs") Instant deletedTs,
            @Param("prefix") String prefix);

    void deleteSessions(@Param("olderThan") Instant olderThan, @Param("prefix") String prefix);
}

package pl.sparkbit.security.dao.mybatis;

import org.apache.ibatis.annotations.Param;
import pl.sparkbit.security.domain.Session;
import pl.sparkbit.security.login.AuthnAttributes;
import pl.sparkbit.security.login.LoginUserDetails;
import pl.sparkbit.security.rest.RestUserDetails;

import java.time.Instant;

public interface SecurityMapper {

    void insertSession(@Param("session") Session session, @Param("prefix") String prefix);

    LoginUserDetails selectLoginUserDetails(@Param("authnAttributes") AuthnAttributes authnAttributes,
                                            @Param("prefix") String prefix);

    RestUserDetails selectRestUserDetails(@Param("authToken") String authToken, @Param("prefix") String prefix);

    Session selectSession(@Param("authToken") String authToken, @Param("prefix") String prefix);

    void deleteSession(@Param("authToken") String authToken, @Param("deletedTs") Instant deletedTs,
                       @Param("prefix") String prefix);

    void deleteSessions(@Param("olderThan") Instant olderThan, @Param("prefix") String prefix);
}

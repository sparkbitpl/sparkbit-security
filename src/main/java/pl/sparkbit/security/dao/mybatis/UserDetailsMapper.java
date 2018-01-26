package pl.sparkbit.security.dao.mybatis;

import org.apache.ibatis.annotations.Param;
import pl.sparkbit.security.domain.RestUserDetails;
import pl.sparkbit.security.login.AuthnAttributes;
import pl.sparkbit.security.login.LoginUserDetails;

public interface UserDetailsMapper {

    LoginUserDetails selectLoginUserDetails(@Param("userId") String userId, @Param("prefix") String prefix);

    RestUserDetails selectRestUserDetails(@Param("authToken") String authToken, @Param("prefix") String prefix);

    String selectUserId(@Param("userTableName") String userTableName,
                        @Param("authnAttributes") AuthnAttributes authnAttributes, @Param("prefix") String prefix);
}

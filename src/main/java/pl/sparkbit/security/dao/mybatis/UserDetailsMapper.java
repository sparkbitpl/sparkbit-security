package pl.sparkbit.security.dao.mybatis;

import org.apache.ibatis.annotations.Param;
import pl.sparkbit.security.domain.RestUserDetails;
import pl.sparkbit.security.login.LoginUserDetails;

import java.util.Map;

public interface UserDetailsMapper {

    LoginUserDetails selectLoginUserDetails(@Param("userId") String userId, @Param("prefix") String prefix);

    RestUserDetails selectRestUserDetails(
            @Param("authTokenHash") String authTokenHash,
            @Param("prefix") String prefix);

    String selectUserId(@Param("userTableName") String userTableName,
                        @Param("userTableIdColumnName") String userTableIdColumnName,
                        @Param("authnAttributes") Map<String, String> authnAttributes, @Param("prefix") String prefix);
}

package pl.sparkbit.security.rest.dao.mybatis;

import org.apache.ibatis.annotations.Param;
import pl.sparkbit.security.rest.domain.RestUserDetails;

import java.util.Map;

public interface RestSecurityMapper {

    void insertCredentials(@Param("userId") String userId, @Param("password") String password,
                           @Param("enabled") boolean enabled, @Param("deleted") boolean deleted,
                           @Param("authnAttributes") Map authnAttributes, @Param("prefix") String prefix);

    void insertUserRole(@Param("userId") String userId, @Param("role") String role);

    RestUserDetails selectRestUserDetails(@Param("authToken") String authToken, @Param("prefix") String prefix);

    String selectPasswordHashForUser(@Param("userId") String userId, @Param("prefix") String prefix);

    void updatePassword(@Param("userId") String userId, @Param("password") String password,
                        @Param("prefix") String prefix);
}

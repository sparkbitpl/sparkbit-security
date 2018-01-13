package pl.sparkbit.security.rest.dao.mybatis;

import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.GrantedAuthority;
import pl.sparkbit.security.rest.domain.Credentials;
import pl.sparkbit.security.rest.domain.RestUserDetails;

public interface RestSecurityMapper {

    void insertCredentials(@Param("credentials") Credentials credentials, @Param("prefix") String prefix);

    void insertUserRole(@Param("userId") String userId, @Param("role") GrantedAuthority role);

    RestUserDetails selectRestUserDetails(@Param("authToken") String authToken, @Param("prefix") String prefix);

    String selectPasswordHashForUser(@Param("userId") String userId, @Param("prefix") String prefix);

    void updateCredentials(@Param("credentials") Credentials credentials, @Param("prefix") String prefix);
}

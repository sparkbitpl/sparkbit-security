package pl.sparkbit.security.dao.mybatis;

import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.GrantedAuthority;
import pl.sparkbit.security.domain.Credentials;

public interface CredentialsMapper {

    void insertCredentials(@Param("credentials") Credentials credentials, @Param("prefix") String prefix);

    void insertUserRole(@Param("userId") String userId, @Param("role") GrantedAuthority role);

    String selectPasswordHashForUser(@Param("userId") String userId, @Param("prefix") String prefix);

    void updateCredentials(@Param("credentials") Credentials credentials, @Param("prefix") String prefix);
}

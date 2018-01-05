package pl.sparkbit.security.rest.dao.mybatis;

import org.apache.ibatis.annotations.Param;
import pl.sparkbit.security.rest.domain.RestUserDetails;

public interface RestSecurityMapper {

    RestUserDetails selectRestUserDetails(@Param("authToken") String authToken, @Param("prefix") String prefix);
}

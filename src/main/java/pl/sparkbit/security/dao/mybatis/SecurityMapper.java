package pl.sparkbit.security.dao.mybatis;

import org.apache.ibatis.annotations.Param;
import pl.sparkbit.security.rest.user.RestUserDetails;

public interface SecurityMapper {

    RestUserDetails selectRestUserDetails(@Param("authToken") String authToken, @Param("prefix") String prefix);
}

package pl.sparkbit.security.dao.mybatis;

import org.apache.ibatis.annotations.Param;
import pl.sparkbit.security.domain.Session;
import pl.sparkbit.security.login.AuthnAttributes;
import pl.sparkbit.security.login.LoginUserDetails;
import pl.sparkbit.security.rest.RestUserDetails;

public interface SecurityMapper {

    void insertSession(Session session);

    LoginUserDetails selectLoginUserDetails(@Param("authnAttributes") AuthnAttributes authnAttributes);

    RestUserDetails selectRestUserDetails(String sessionId);

    Session selectSession(String i);

    void deleteSession(String sessionId);
}

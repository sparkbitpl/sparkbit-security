package pl.sparkbit.security.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import pl.sparkbit.security.dao.SecurityDao;
import pl.sparkbit.security.dao.mybatis.SecurityMapper;
import pl.sparkbit.security.rest.user.RestUserDetails;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SecurityDaoImpl implements SecurityDao {

    private final SecurityMapper securityMapper;

    @Value("${sparkbit.security.user-entity-name:user}")
    private String prefix;

    @Override
    public Optional<RestUserDetails> selectRestUserDetails(String authToken) {
        return Optional.ofNullable(securityMapper.selectRestUserDetails(authToken, prefix));
    }
}

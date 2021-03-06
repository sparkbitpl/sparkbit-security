package pl.sparkbit.security.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.sparkbit.security.config.SecurityProperties;
import pl.sparkbit.security.dao.UserDetailsDao;
import pl.sparkbit.security.dao.mybatis.UserDetailsMapper;
import pl.sparkbit.security.domain.RestUserDetails;
import pl.sparkbit.security.login.LoginUserDetails;

import java.util.Map;
import java.util.Optional;

import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@RequiredArgsConstructor
@SuppressWarnings("unused")
@Transactional(propagation = MANDATORY)
public class UserDetailsDaoImpl implements UserDetailsDao {

    private final UserDetailsMapper userDetailsMapper;
    private final SecurityProperties configuration;

    @Override
    public Optional<LoginUserDetails> selectLoginUserDetails(String userId) {
        return Optional.ofNullable(userDetailsMapper.selectLoginUserDetails(userId,
                configuration.getDatabaseSchema().getUserEntityName()));
    }

    @Override
    public Optional<RestUserDetails> selectRestUserDetails(String authTokenHash) {
        return Optional.ofNullable(userDetailsMapper.selectRestUserDetails(authTokenHash,
                configuration.getDatabaseSchema().getUserEntityName()));
    }

    @Override
    public Optional<String> selectUserId(Map<String, String> authnAttributes) {
        return Optional.ofNullable(userDetailsMapper.selectUserId(
                configuration.getDatabaseSchema().getUserTableName(),
                configuration.getDatabaseSchema().getUserTableIdColumnName(),
                authnAttributes,
                configuration.getDatabaseSchema().getUserEntityName()));
    }
}

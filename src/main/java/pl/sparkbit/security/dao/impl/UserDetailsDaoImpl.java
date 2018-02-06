package pl.sparkbit.security.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.sparkbit.security.dao.UserDetailsDao;
import pl.sparkbit.security.dao.mybatis.UserDetailsMapper;
import pl.sparkbit.security.domain.RestUserDetails;
import pl.sparkbit.security.login.AuthnAttributes;
import pl.sparkbit.security.login.LoginUserDetails;

import java.util.Optional;

import static org.springframework.transaction.annotation.Propagation.MANDATORY;
import static pl.sparkbit.security.config.Properties.USER_ENTITY_NAME;
import static pl.sparkbit.security.config.Properties.USER_TABLE_ID_COLUMN_NAME;
import static pl.sparkbit.security.config.Properties.USER_TABLE_NAME;

@Repository
@RequiredArgsConstructor
@SuppressWarnings("unused")
@Transactional(propagation = MANDATORY)
public class UserDetailsDaoImpl implements UserDetailsDao {

    private final UserDetailsMapper userDetailsMapper;

    @Value("${" + USER_ENTITY_NAME + ":user}")
    private String prefix;
    @Value("${" + USER_TABLE_NAME + ":uzer}")
    private String userTableName;
    @Value("${" + USER_TABLE_ID_COLUMN_NAME + ":id}")
    private String userTableIdColumnName;

    @Override
    public Optional<LoginUserDetails> selectLoginUserDetails(String userId) {
        return Optional.ofNullable(userDetailsMapper.selectLoginUserDetails(userId, prefix));
    }

    @Override
    public Optional<RestUserDetails> selectRestUserDetails(String authToken) {
        return Optional.ofNullable(userDetailsMapper.selectRestUserDetails(authToken, prefix));
    }

    @Override
    public Optional<String> selectUserId(AuthnAttributes authnAttributes) {
        return Optional.ofNullable(userDetailsMapper.selectUserId(userTableName, userTableIdColumnName, authnAttributes,
                prefix));
    }
}

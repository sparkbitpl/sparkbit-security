package pl.sparkbit.security.dao.mybatis.data;

import com.ninja_squad.dbsetup.operation.CompositeOperation;
import com.ninja_squad.dbsetup.operation.Operation;

import static pl.sparkbit.security.dao.mybatis.data.SecurityTestDataUtils.role;
import static pl.sparkbit.security.dao.mybatis.data.SecurityTestDataUtils.user;

public class SecurityTestData {

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

    public static final String USER_1_ID = "user0000000000000000000000000001";
    public static final String USER_1_USERNAME = "user0";
    public static final String USER_1_PASSWORD = "secret";
    public static final Operation USER_1 = CompositeOperation.sequenceOf(
            user(SampleUser.builder().id(USER_1_ID).username(USER_1_USERNAME).password(USER_1_PASSWORD).build()),
            role(USER_1_ID, ROLE_USER), role(USER_1_ID, ROLE_ADMIN));
}

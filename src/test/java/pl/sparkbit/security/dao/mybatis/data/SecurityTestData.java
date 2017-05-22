package pl.sparkbit.security.dao.mybatis.data;

import com.ninja_squad.dbsetup.operation.CompositeOperation;
import com.ninja_squad.dbsetup.operation.Operation;

import static pl.sparkbit.security.dao.mybatis.data.SecurityTestDataUtils.role;
import static pl.sparkbit.security.dao.mybatis.data.SecurityTestDataUtils.user;

public class SecurityTestData {

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

    public static final String CREDS_1_USER_ID = "user0000000000000000000000000001";
    public static final String CREDS_1_USERNAME = "user0";
    public static final String CREDS_1_PASSWORD = "secret";
    public static final Operation CREDS_1 = CompositeOperation.sequenceOf(
            user(Credentials.builder().userId(CREDS_1_USER_ID).username(CREDS_1_USERNAME).password(CREDS_1_PASSWORD)
                    .build()),
            role(CREDS_1_USER_ID, ROLE_USER), role(CREDS_1_USER_ID, ROLE_ADMIN));
}

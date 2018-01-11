package pl.sparkbit.security.rest.dao.mybatis.data;

import com.ninja_squad.dbsetup.operation.CompositeOperation;
import com.ninja_squad.dbsetup.operation.Operation;
import pl.sparkbit.security.challenge.domain.SecurityChallengeType;

import java.time.Instant;

import static pl.sparkbit.security.challenge.domain.SecurityChallengeType.EMAIL_VERIFICATION;
import static pl.sparkbit.security.rest.dao.mybatis.data.SecurityTestDataUtils.*;

public class SecurityTestData {

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

    public static final String CREDS_1_USER_ID = "user0000000000000000000000000001";
    public static final String CREDS_1_PASSWORD = "secret";
    public static final Boolean CREDS_1_ENABLED = true;
    public static final Boolean CREDS_1_DELETED = false;
    public static final Operation CREDS_1 = CompositeOperation.sequenceOf(
            user(Credentials.builder().userId(CREDS_1_USER_ID).password(CREDS_1_PASSWORD)
                    .enabled(CREDS_1_ENABLED).deleted(CREDS_1_DELETED).build()),
            role(CREDS_1_USER_ID, ROLE_USER), role(CREDS_1_USER_ID, ROLE_ADMIN));

    public static final String CHALLENGE_1_ID = "idddddddd";
    public static final SecurityChallengeType CHALLENGE_1_TYPE = EMAIL_VERIFICATION;
    public static final Instant CHALLENGE_1_EXPIRATION_TIMESTAMP = Instant.ofEpochSecond(12345);
    public static final String CHALLENGE_1_TOKEN = "asdasfsfasfas";

    public static final Operation CHALLENGE_1 = securityChallenge(CHALLENGE_1_ID, CREDS_1_USER_ID, CHALLENGE_1_TYPE,
            CHALLENGE_1_EXPIRATION_TIMESTAMP, CHALLENGE_1_TOKEN);

    public static final String USER_1_ID = "12345";
    public static final String USER_1_USERNAME = "aa@bb.cc";
    public static final String USER_1_CONTEXT = "some_context";

    public static final Operation SIMPLE_LOGIN_USER_1 = simpleLoginUser(USER_1_ID, USER_1_USERNAME);
    public static final Operation COMPOSITE_LOGIN_USER_1 =
            compositeLoginUser(USER_1_ID, USER_1_USERNAME, USER_1_CONTEXT);
}

package pl.sparkbit.security.dao.mybatis.data;

import com.ninja_squad.dbsetup.operation.Operation;
import pl.sparkbit.security.domain.Credentials;
import pl.sparkbit.security.domain.SecurityChallengeType;

import java.time.Instant;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static pl.sparkbit.security.dao.mybatis.data.SecurityDbTables.*;

@SuppressWarnings("WeakerAccess")
public class SecurityTestDataUtils {

    static Operation user(Credentials u) {
        return insertInto(CREDENTIALS)
                .columns("user_id", "password", "enabled", "deleted")
                .values(u.getUserId(), u.getPassword(), u.getEnabled(), u.getDeleted())
                .build();
    }

    public static Operation role(String userId, String role) {
        return insertInto(USER_ROLE)
                .columns("user_id", "role")
                .values(userId, role)
                .build();
    }

    public static Operation session(String authToken, String userId, Instant creation) {
        return session(authToken, userId, creation, null);
    }

    public static Operation session(String authToken, String user_id, Instant creation, Instant deleted) {
        Long deletedTs = deleted == null ? null : deleted.toEpochMilli();
        return insertInto(SESSION)
                .columns("auth_token", "user_id", "creation_ts", "deleted_ts")
                .values(authToken, user_id, creation.toEpochMilli(), deletedTs)
                .build();
    }

    public static Operation securityChallenge(String id, String userId, SecurityChallengeType type,
                                              Instant expirationTimestamp, String token) {
        return insertInto(SECURITY_CHALLENGE)
                .columns("id", "user_id", "challenge_type", "expiration_ts", "token")
                .values(id, userId, type, expirationTimestamp.toEpochMilli(), token)
                .build();
    }

    public static Operation simpleLoginUser(String id, String username) {
        return insertInto(SIMPLE_LOGIN_USER)
                .columns("id", "username")
                .values(id, username)
                .build();
    }

    public static Operation compositeLoginUser(String id, String username, String context) {
        return insertInto(COMPOSITE_LOGIN_USER)
                .columns("id", "username", "context")
                .values(id, username, context)
                .build();
    }
}

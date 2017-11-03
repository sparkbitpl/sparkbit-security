package pl.sparkbit.security.dao.mybatis.data;

import com.ninja_squad.dbsetup.operation.Operation;

import java.time.Instant;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static pl.sparkbit.security.dao.mybatis.data.SecurityDbTables.SESSION;
import static pl.sparkbit.security.dao.mybatis.data.SecurityDbTables.CREDENTIALS;
import static pl.sparkbit.security.dao.mybatis.data.SecurityDbTables.USER_ROLE;

public class SecurityTestDataUtils {

    static Operation user(Credentials u) {
        return insertInto(CREDENTIALS)
                .columns("user_id", "username", "password", "enabled", "deleted")
                .values(u.getUserId(), u.getUsername(), u.getPassword(), u.getEnabled(), u.getDeleted())
                .build();
    }

    public static Operation role(String userId, String role) {
        return insertInto(USER_ROLE)
                .columns("user_id", "role")
                .values(userId, role)
                .build();
    }

    public static Operation session(String authToken, String user_id, Instant creation) {
        return insertInto(SESSION)
                .columns("auth_token", "user_id", "creation_ts")
                .values(authToken, user_id, creation.toEpochMilli())
                .build();
    }
}

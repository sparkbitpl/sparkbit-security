package pl.sparkbit.security.dao.mybatis.data;

import com.ninja_squad.dbsetup.operation.Operation;

import java.time.Instant;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static pl.sparkbit.security.dao.mybatis.data.SecurityDbTables.SESSION;
import static pl.sparkbit.security.dao.mybatis.data.SecurityDbTables.USER;
import static pl.sparkbit.security.dao.mybatis.data.SecurityDbTables.USER_ROLE;

public class SecurityTestDataUtils {

    public static Operation session(String id, String user_id, Instant creation) {
        return insertInto(SESSION)
                .columns("id", "user_id", "creation_ts")
                .values(id, user_id, creation.toEpochMilli())
                .build();
    }

    static Operation user(SampleUser u) {
        return insertInto(USER)
                .columns("id", "username", "password")
                .values(u.getId(), u.getUsername(), u.getPassword())
                .build();
    }

    public static Operation role(String userId, String role) {
        return insertInto(USER_ROLE)
                .columns("user_id", "role")
                .values(userId, role)
                .build();
    }
}

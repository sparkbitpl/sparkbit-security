package pl.sparkbit.security.dao.mybatis.data;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class SecurityDbTables {

    public static final String SESSION = "session";
    public static final String USER_ROLE = "user_role";
    public static final String CREDENTIALS = "credentials";
}

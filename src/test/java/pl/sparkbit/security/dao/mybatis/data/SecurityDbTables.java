package pl.sparkbit.security.dao.mybatis.data;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class SecurityDbTables {

    public static final String PREFIX = "user";

    public static final String SESSION = PREFIX + "_session";
    public static final String USER_ROLE = PREFIX + "_role";
    public static final String CREDENTIALS = PREFIX + "_credentials";
    public static final String SECURITY_CHALLENGE = "security_challenge";

    public static final String SIMPLE_LOGIN_USER = "simple_login_user";
    public static final String COMPOSITE_LOGIN_USER = "composite_login_user";
}

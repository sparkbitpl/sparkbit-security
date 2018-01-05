package pl.sparkbit.security.rest.dao.mybatis.data;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class SecurityDbTables {

    public static final String PREFIX = "user";

    public static final String SESSION = PREFIX + "_session";
    public static final String USER_ROLE = PREFIX + "_role";
    public static final String CREDENTIALS = PREFIX + "_credentials";
}

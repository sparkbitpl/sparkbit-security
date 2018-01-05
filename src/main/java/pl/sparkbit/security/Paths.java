package pl.sparkbit.security;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@SuppressWarnings("checkstyle:hideutilityclassconstructor")
public class Paths {

    //session
    public static final String LOGIN = "/login";
    public static final String LOGOUT = "/logout";

    //password
    public static final String PASSWORD = "/profile/password";
}

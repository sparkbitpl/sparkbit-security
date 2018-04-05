package pl.sparkbit.security.mvc.controller;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@SuppressWarnings({"checkstyle:hideutilityclassconstructor", "WeakerAccess"})
public class Paths {

    //session
    public static final String LOGIN = "/login";
    public static final String LOGOUT = "/logout";

    //password
    public static final String PASSWORD = "/profile/password";

    //extra authn check
    public static final String EXTRA_AUTH_CHECK = "/extraAuthCheck";

    //challenge
    public static final String PUBLIC_EMAIL = "/public/profile/email";
    public static final String PUBLIC_PASSWORD_RESET_TOKEN = "/public/profile/passwordResetToken";
    public static final String PUBLIC_PASSWORD = "/public/profile/password";
}

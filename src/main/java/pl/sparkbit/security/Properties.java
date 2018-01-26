package pl.sparkbit.security;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@SuppressWarnings("checkstyle:hideutilityclassconstructor")
public class Properties {

    public static final String AUTH_TOKEN_HEADER_NAME = "sparkbit.security.authTokenHeader.name";
    public static final String AUTH_TOKEN_COOKIE_NAME = "sparkbit.security.authTokenCookie.name";
}

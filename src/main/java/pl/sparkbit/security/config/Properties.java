package pl.sparkbit.security.config;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@SuppressWarnings({"checkstyle:hideutilityclassconstructor", "WeakerAccess"})
public class Properties {

    private static final String PREFIX = "sparkbit.security.";

    public static final String ALLOW_UNSECURED_COOKIE = PREFIX + "allowUnsecuredCookie";
    public static final String AUTH_TOKEN_HEADER_NAME = PREFIX + "authTokenHeader.name";
    public static final String AUTH_TOKEN_COOKIE_NAME = PREFIX + "authTokenCookie.name";
    public static final String CHALLENGE_DELETER_RUN_EVERY_MILLIS = PREFIX + "challenge.deleter.runEveryMillis";
    public static final String CHALLENGE_TOKEN_ALLOWED_CHARACTERS = PREFIX + "challengeToken.allowedCharacters";
    public static final String CHALLENGE_TOKEN_LENGTH = PREFIX + "challengeToken.length";
    public static final String CORS_ALLOW_CREDENTIALS = PREFIX + "cors.allowCredentials";
    public static final String CORS_ALLOWED_HEADERS = PREFIX + "cors.allowedHeaders";
    public static final String CORS_ALLOWED_METHODS = PREFIX + "cors.allowedMethods";
    public static final String CORS_ALLOWED_ORIGINS = PREFIX + "cors.allowedOrigins";
    public static final String CORS_MAX_AGE_SECONDS = PREFIX + "cors.maxAgeSeconds";
    public static final String CORS_EXPOSED_HEADERS = PREFIX + "cors.exposedHeaders";
    public static final String DEFAULT_PASSWORD_POLICY_ENABLED = PREFIX + "defaultPasswordPolicy.enabled";
    public static final String EMAIL_VERIFICATION_CHALLENGE_VALIDITY_HOURS =
            PREFIX + "emailVerification.challengeValidityHours";
    public static final String EMAIL_VERIFICATION_ENABLED = PREFIX + "emailVerification.enabled";
    public static final String EXPECTED_AUTHN_ATTRIBUTES = PREFIX + "expected-authn-attributes";
    public static final String MINIMAL_PASSWORD_LENGTH = PREFIX + "minPasswordLength";
    public static final String PASSWORD_CHANGE_ENABLED = PREFIX + "passwordChange.enabled";
    public static final String PASSWORD_ENCODER_TYPE = PREFIX + "passwordEncoderType";
    public static final String PASSWORD_RESET_CHALLENGE_VALIDITY_HOURS =
            PREFIX + "passwordReset.challengeValidityHours";
    public static final String PASSWORD_RESET_ENABLED = PREFIX + "passwordReset.enabled";
    public static final String SESSION_DELETER_OLDER_THAN_MINUTES = PREFIX + "session.deleter.olderThanMinutes";
    public static final String SESSION_DELETER_REMOVE_OLD = PREFIX + "session.deleter.removeExpired";
    public static final String SESSION_DELETER_RUN_EVERY_MILLIS = PREFIX + "session.deleter.runEveryMillis";
    public static final String SESSION_EXPIRATION_MINUTES = PREFIX + "session.expirationMinutes";
    public static final String SESSION_EXPIRES_AT_HEADER_NAME = PREFIX + "sessionExpiresAtHeader.name";
    public static final String USER_ENTITY_NAME = PREFIX + "user-entity-name";
    public static final String USER_TABLE_NAME = PREFIX + "userTableName";
    public static final String USER_TABLE_ID_COLUMN_NAME = PREFIX + "userTableIdColumnName";

}

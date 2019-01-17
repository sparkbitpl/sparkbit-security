package pl.sparkbit.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import pl.sparkbit.security.password.encoder.PasswordEncoderType;

import javax.validation.constraints.NotNull;
import java.time.Duration;

@Component
@ConfigurationProperties("sparkbit.security")
@Data
@SuppressWarnings("WeakerAccess")
@Validated
public class SecurityProperties {

    private static final String PREFIX = "sparkbit.security.";

    public static final String DEFAULT_PASSWORD_POLICY_ENABLED = PREFIX + "default-password-policy.enabled";
    public static final String DELETED_SESSIONS_PURGING_ENABLED = PREFIX + "deleted-session-purging.enabled";
    public static final String EMAIL_VERIFICATION_ENABLED = PREFIX + "email-verification.enabled";
    public static final String EXTRA_AUTHENTICATION_CHECK_ENABLED = PREFIX + "extra-authn-check.enabled";
    public static final String PASSWORD_CHANGE_ENABLED = PREFIX + "password-change-enabled";
    public static final String PASSWORD_RESET_ENABLED = PREFIX + "password-reset.enabled";
    public static final String SESSION_EXPIRATION_ENABLED = PREFIX + "session-expiration.enabled";

    @NotNull
    private AuthCookie authCookie;
    @NotNull
    private String authTokenHeaderName = "X-Sparkbit-Auth-Token";
    @NotNull
    private ChallengeToken challengeToken;
    @NotNull
    private Cors cors;
    @NotNull
    private DeletedSessionPurging deletedSessionPurging;
    @NotNull
    private EmailVerification emailVerification;
    @NotNull
    private String expectedAuthnAttributes = "email";
    @NotNull
    private ExpiredChallengeDeletion expiredChallengeDeletion;
    @NotNull
    private ExtraAuthnCheck extraAuthnCheck;
    @NotNull
    private Boolean passwordChangeEnabled = true;
    @NotNull
    private PasswordEncoderType passwordEncoderType = PasswordEncoderType.BCRYPT;
    @NotNull
    private PasswordReset passwordReset;
    @NotNull
    private SessionExpiration sessionExpiration;
    @NotNull
    private DefaultPasswordPolicy defaultPasswordPolicy;
    @NotNull
    private DatabaseSchema databaseSchema;
    @NotNull
    private Paths paths;

    @Data
    @Validated
    public static class AuthCookie {
        @NotNull
        private String name = "sparkbitAuthToken";
        @NotNull
        private String path = "/";
        @NotNull
        private Boolean allowUnsecured = false;
    }

    @Data
    @Validated
    public static class DatabaseSchema {
        @NotNull
        private String userEntityName = "user";
        @NotNull
        private String userTableName = "uzer";
        @NotNull
        private String userTableIdColumnName = "id";
    }

    @Data
    @Validated
    public static class DefaultPasswordPolicy {
        @NotNull
        private Boolean enabled = true;
        @NotNull
        private Integer minPasswordLength = 8;
    }

    @Data
    @Validated
    public static class ChallengeToken {
        @NotNull
        private String allowedCharacters = "23456789ABCDEFGHIJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
        @NotNull
        private Integer length = 6;
    }

    @Data
    @Validated
    public static class Cors {
        @NotNull
        private Boolean allowCredentials = true;
        @NotNull
        private String allowedHeaders = "*";
        @NotNull
        private String[] allowedMethods = {"GET", "POST", "PUT", "DELETE", "HEAD"};
        @NotNull
        private String allowedOrigins = "*";
        @NotNull
        private String exposedHeaders = "X-Sparkbit-Session-Expiration-Timestamp";
        @NotNull
        private Duration maxAge = Duration.ofMinutes(30);
    }

    @Data
    @Validated
    public static class DeletedSessionPurging {
        @NotNull
        private Boolean enabled = true;
        @NotNull
        private Duration olderThan = Duration.ofDays(7);
        @NotNull
        private Duration runEvery = Duration.ofHours(1);
    }

    @Data
    @Validated
    public static class EmailVerification {
        @NotNull
        private Duration challengeValidity = Duration.ofHours(1);
        @NotNull
        private Boolean enabled = false;
    }

    @Data
    @Validated
    public static class ExpiredChallengeDeletion {
        @NotNull
        private Duration runEvery = Duration.ofHours(1);
    }

    @Data
    @Validated
    public static class ExtraAuthnCheck {
        @NotNull
        private Duration challengeValidity = Duration.ofHours(1);
        @NotNull
        private Boolean enabled = false;
    }

    @Data
    @Validated
    public static class PasswordReset {
        @NotNull
        private Duration challengeValidity = Duration.ofHours(1);
        @NotNull
        private Boolean informNotFound = false;
        @NotNull
        private Boolean enabled = false;
    }

    @Data
    @Validated
    public static class SessionExpiration {
        @NotNull
        private Boolean enabled = false;
        @NotNull
        private Duration duration = Duration.ofHours(1);
        @NotNull
        private String timestampHeaderName = "X-Sparkbit-Session-Expiration-Timestamp";
    }

    @Data
    @Validated
    public static class Paths {
        @NotNull
        private String publicPrefix = "/public";
        @NotNull
        private String adminPrefix = "/admin";
        @NotNull
        private String login = "/login";
        @NotNull
        private String logout = "/logout";
        @NotNull
        private String extraAuthCheck = "/extraAuthCheck";
        @NotNull
        private String password = "/profile/password";
        @NotNull
        private String publicPasswordResetToken = "/profile/passwordResetToken";
        @NotNull
        private String publicPassword = "/profile/password";
        @NotNull
        private String publicEmail = "/profile/email";
    }
}

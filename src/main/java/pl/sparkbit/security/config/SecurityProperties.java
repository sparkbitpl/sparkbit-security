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
    private String authTokenHeaderName;
    @NotNull
    private ChallengeToken challengeToken;
    @NotNull
    private Cors cors;
    @NotNull
    private DeletedSessionPurging deletedSessionPurging;
    @NotNull
    private EmailVerification emailVerification;
    @NotNull
    private String expectedAuthnAttributes;
    @NotNull
    private ExpiredChallengeDeletion expiredChallengeDeletion;
    @NotNull
    private ExtraAuthnCheck extraAuthnCheck;
    @NotNull
    private Boolean passwordChangeEnabled;
    @NotNull
    private PasswordEncoderType passwordEncoderType;
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
        private String name;
        @NotNull
        private String path;
        @NotNull
        private Boolean allowUnsecured;
    }

    @Data
    @Validated
    public static class DatabaseSchema {
        @NotNull
        private String userEntityName;
        @NotNull
        private String userTableName;
        @NotNull
        private String userTableIdColumnName;
    }

    @Data
    @Validated
    public static class DefaultPasswordPolicy {
        @NotNull
        private Boolean enabled;
        @NotNull
        private Integer minPasswordLength;
    }

    @Data
    @Validated
    public static class ChallengeToken {
        @NotNull
        private String allowedCharacters;
        @NotNull
        private Integer length;
    }

    @Data
    @Validated
    public static class Cors {
        @NotNull
        private Boolean allowCredentials;
        @NotNull
        private String allowedHeaders;
        @NotNull
        private String allowedMethods;
        @NotNull
        private String allowedOrigins;
        @NotNull
        private String exposedHeaders;
        @NotNull
        private Duration maxAge;
    }

    @Data
    @Validated
    public static class DeletedSessionPurging {
        @NotNull
        private Boolean enabled;
        @NotNull
        private Duration olderThan;
        @NotNull
        private Duration runEvery;

    }

    @Data
    @Validated
    public static class EmailVerification {
        @NotNull
        private Duration challengeValidity;
        @NotNull
        private Boolean enabled;
    }

    @Data
    @Validated
    public static class ExpiredChallengeDeletion {
        @NotNull
        private Duration runEvery;
    }

    @Data
    @Validated
    public static class ExtraAuthnCheck {
        @NotNull
        private Duration challengeValidity;
        @NotNull
        private Boolean enabled;
    }

    @Data
    @Validated
    public static class PasswordReset {
        @NotNull
        private Duration challengeValidity;
        @NotNull
        private Boolean informNotFound;
        @NotNull
        private Boolean enabled;
    }

    @Data
    @Validated
    public static class SessionExpiration {
        @NotNull
        private Boolean enabled;
        @NotNull
        private Duration duration;
        @NotNull
        private String timestampHeaderName;
    }

    @Data
    @Validated
    public static class Paths {
        @NotNull
        private String commonPrefix;
        @NotNull
        private String publicPrefix;
        @NotNull
        private String adminPrefix;
        @NotNull
        private String login;
        @NotNull
        private String logout;
        @NotNull
        private String extraAuthCheck;
        @NotNull
        private String password;
        @NotNull
        private String publicPasswordResetToken;
        @NotNull
        private String publicPassword;
        @NotNull
        private String publicEmail;
        @NotNull
        private String buildInfo;
    }
}

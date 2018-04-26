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
public class Properties {

    private static final String PREFIX = "sparkbit.security.";

    public static final String DEFAULT_PASSWORD_POLICY_ENABLED = PREFIX + "default-password-policy-enabled";
    public static final String DELETED_SESSIONS_PURGING_ENABLED = PREFIX + "deleted-session-purging.enabled";
    public static final String EMAIL_VERIFICATION_ENABLED = PREFIX + "email-verification.enabled";
    public static final String EXTRA_AUTHENTICATION_CHECK_ENABLED = PREFIX + "extra-authn-check.enabled";
    public static final String PASSWORD_CHANGE_ENABLED = PREFIX + "password-change.enabled";
    public static final String PASSWORD_RESET_ENABLED = PREFIX + "password-reset.enabled";
    public static final String SESSION_EXPIRATION_ENABLED = PREFIX + "session-expiration.enabled";

    @NotNull
    private boolean allowUnsecuredCookie;
    @NotNull
    private String authTokenHeaderName;
    @NotNull
    private String authTokenCookieName;
    @NotNull
    private ChallengeToken challengeToken;
    @NotNull
    private Cors cors;
    @NotNull
    private boolean defaultPasswordPolicyEnabled;
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
    private Integer minPasswordLength;
    @NotNull
    private boolean passwordChangeEnabled;
    @NotNull
    private PasswordEncoderType passwordEncoderType;
    @NotNull
    private PasswordReset passwordReset;
    @NotNull
    private SessionExpiration sessionExpiration;
    @NotNull
    private String userEntityName;
    @NotNull
    private String userTableName;
    @NotNull
    private String userTableIdColumnName;

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
        private boolean allowCredentials;
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
        private boolean enabled;
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
        private boolean enabled;
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
        private boolean enabled;
    }

    @Data
    @Validated
    public static class PasswordReset {
        @NotNull
        private Duration challengeValidity;
        @NotNull
        private boolean informNotFound;
        @NotNull
        private boolean enabled;
    }

    @Data
    @Validated
    public static class SessionExpiration {
        @NotNull
        private boolean enabled;
        @NotNull
        private Duration duration;
        @NotNull
        private String timestampHeaderName;
    }
}

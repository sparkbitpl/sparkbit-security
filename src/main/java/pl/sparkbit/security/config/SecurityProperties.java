package pl.sparkbit.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import pl.sparkbit.security.password.encoder.PasswordEncoderType;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Collections;
import java.util.Set;

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
    @Valid
    private AuthCookie authCookie = new AuthCookie();
    @NotNull
    @Valid
    private String authTokenHeaderName = "X-Sparkbit-Auth-Token";
    @NotNull
    @Valid
    private ChallengeToken challengeToken = new ChallengeToken();
    @NotNull
    @Valid
    private Cors cors = new Cors();
    @NotNull
    @Valid
    private DeletedSessionPurging deletedSessionPurging = new DeletedSessionPurging();
    @NotNull
    @Valid
    private EmailVerification emailVerification = new EmailVerification();
    @NotNull
    @Valid
    private Set<String> expectedAuthnAttributes = Collections.singleton("email");
    @NotNull
    @Valid
    private ExpiredChallengeDeletion expiredChallengeDeletion = new ExpiredChallengeDeletion();
    @NotNull
    @Valid
    private ExtraAuthnCheck extraAuthnCheck = new ExtraAuthnCheck();
    @NotNull
    @Valid
    private Boolean passwordChangeEnabled = true;
    @NotNull
    @Valid
    private PasswordEncoderType passwordEncoderType = PasswordEncoderType.BCRYPT;
    @NotNull
    @Valid
    private PasswordReset passwordReset = new PasswordReset();
    @NotNull
    @Valid
    private SessionExpiration sessionExpiration = new SessionExpiration();
    @NotNull
    @Valid
    private DefaultPasswordPolicy defaultPasswordPolicy = new DefaultPasswordPolicy();
    @NotNull
    @Valid
    private DatabaseSchema databaseSchema = new DatabaseSchema();
    @NotNull
    @Valid
    private Paths paths = new Paths();

    @Data
    public static class AuthCookie {
        @NotNull
        private String name = "sparkbitAuthToken";
        @NotNull
        private String path = "/";
        @NotNull
        private Boolean allowUnsecured = false;
    }

    @Data
    public static class DatabaseSchema {
        @NotNull
        private String userEntityName = "user";
        @NotNull
        private String userTableName = "uzer";
        @NotNull
        private String userTableIdColumnName = "id";
    }

    @Data
    public static class DefaultPasswordPolicy {
        @NotNull
        private Boolean enabled = true;
        @NotNull
        private Integer minPasswordLength = 8;
    }

    @Data
    public static class ChallengeToken {
        @NotNull
        private String allowedCharacters = "23456789ABCDEFGHIJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
        @NotNull
        private Integer length = 6;
    }

    @Data
    public static class Cors {
        @NotNull
        private Boolean allowCredentials = true;
        @NotNull
        private String allowedHeaders = "*";
        @NotNull
        private String[] allowedMethods = {"GET", "POST", "PUT", "DELETE", "HEAD"};
        @NotNull
        private String allowedOrigins = "";
        @NotNull
        private String allowedOriginPatterns = "*";
        @NotNull
        private String exposedHeaders = "X-Sparkbit-Session-Expiration-Timestamp";
        @NotNull
        private Duration maxAge = Duration.ofMinutes(30);
    }

    @Data
    public static class DeletedSessionPurging {
        @NotNull
        private Boolean enabled = true;
        @NotNull
        private Duration olderThan = Duration.ofDays(7);
        @NotNull
        private Duration runEvery = Duration.ofHours(1);
    }

    @Data
    public static class EmailVerification {
        @NotNull
        private Duration challengeValidity = Duration.ofHours(1);
        @NotNull
        private Boolean enabled = false;
    }

    @Data
    public static class ExpiredChallengeDeletion {
        @NotNull
        private Duration runEvery = Duration.ofHours(1);
    }

    @Data
    public static class ExtraAuthnCheck {
        @NotNull
        private Duration challengeValidity = Duration.ofHours(1);
        @NotNull
        private Boolean enabled = false;
    }

    @Data
    public static class PasswordReset {
        @NotNull
        private Duration challengeValidity = Duration.ofHours(1);
        @NotNull
        private Boolean informNotFound = false;
        @NotNull
        private Boolean enabled = false;
    }

    @Data
    public static class SessionExpiration {
        @NotNull
        private Boolean enabled = false;
        @NotNull
        private Duration duration = Duration.ofHours(1);
        @NotNull
        private String timestampHeaderName = "X-Sparkbit-Session-Expiration-Timestamp";
    }

    @Data
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

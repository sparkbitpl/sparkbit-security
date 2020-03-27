package pl.sparkbit.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.junit.Test;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import pl.sparkbit.commons.util.IdGenerator;
import pl.sparkbit.commons.util.IdGeneratorImpl;
import pl.sparkbit.security.Security;
import pl.sparkbit.security.callbacks.EmailVerificationChallengeCallback;
import pl.sparkbit.security.callbacks.ExtraAuthnCheckChallengeCallback;
import pl.sparkbit.security.callbacks.PasswordResetChallengeCallback;
import pl.sparkbit.security.dao.CredentialsDao;
import pl.sparkbit.security.dao.SecurityChallengeDao;
import pl.sparkbit.security.dao.SessionDao;
import pl.sparkbit.security.dao.UserDetailsDao;
import pl.sparkbit.security.domain.SecurityChallenge;
import pl.sparkbit.security.jobs.DeletedSessionsPurgingJob;
import pl.sparkbit.security.jobs.ExpiredChallengesDeletionJob;
import pl.sparkbit.security.jobs.ExpiredSessionsDeletionJob;
import pl.sparkbit.security.login.LoginPrincipalFactory;
import pl.sparkbit.security.mvc.SecurityEndpointsRegistrations;
import pl.sparkbit.security.mvc.controller.EmailVerificationController;
import pl.sparkbit.security.mvc.controller.ExtraAuthnCheckController;
import pl.sparkbit.security.mvc.controller.PasswordResetController;
import pl.sparkbit.security.mvc.controller.SessionController;
import pl.sparkbit.security.password.encoder.AuthTokenHasher;
import pl.sparkbit.security.password.policy.PasswordPolicy;
import pl.sparkbit.security.restauthn.AuthenticationTokenHelper;
import pl.sparkbit.security.service.*;
import pl.sparkbit.security.util.SecureRandomStringGenerator;

import javax.sql.DataSource;
import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThat;

public class SparkbitSecurityAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withUserConfiguration(TestDataSourceConfig.class)
            .withConfiguration(AutoConfigurations.of(
                    WebMvcAutoConfiguration.class,
                    SecurityAutoConfiguration.class,
                    MybatisAutoConfiguration.class,
                    SparkbitSecurityAutoConfiguration.class,
                    SecurityProperties.class
            ));

    @Test
    public void testDefaultConfig() {
        this.contextRunner.run(context -> assertThat(context)
                .hasSingleBean(AuthenticationEntryPoint.class)
                .hasSingleBean(AuthTokenHasher.class)
                .hasSingleBean(LoginPrincipalFactory.class)
                .hasSingleBean(SecurityEndpointsRegistrations.class)
                .hasSingleBean(AuthenticationTokenHelper.class)
                .hasSingleBean(PasswordEncoder.class)
                .hasSingleBean(DaoAuthenticationProvider.class)
                .hasSingleBean(SecureRandomStringGenerator.class)
                .hasSingleBean(Security.class)
                .hasSingleBean(PasswordPolicy.class)
                //dao
                .hasSingleBean(CredentialsDao.class)
                .hasSingleBean(SecurityChallengeDao.class)
                .hasSingleBean(SessionDao.class)
                .hasSingleBean(UserDetailsDao.class)
                //services
                .hasSingleBean(ConversionService.class)
                .hasSingleBean(UserDetailsService.class)
                .hasSingleBean(SessionService.class)
                .doesNotHaveBean(EmailVerificationService.class)
                .doesNotHaveBean(ExtraAuthnCheckService.class)
                .hasSingleBean(CredentialsService.class)
                .hasSingleBean(PasswordChangeService.class)
                .doesNotHaveBean(PasswordResetService.class)
                //controllers
                .doesNotHaveBean(EmailVerificationController.class)
                .doesNotHaveBean(ExtraAuthnCheckController.class)
                .hasSingleBean(PasswordChangeService.class)
                .doesNotHaveBean(PasswordResetController.class)
                .hasSingleBean(SessionController.class)
                //jobs
                .hasSingleBean(DeletedSessionsPurgingJob.class)
                .hasSingleBean(ExpiredChallengesDeletionJob.class)
                .doesNotHaveBean(ExpiredSessionsDeletionJob.class)
        );
    }

    @Test
    public void testSessionExpirationEnabled() {
        this.contextRunner.withPropertyValues("sparkbit.security.session-expiration.enabled=true")
                .run(context -> assertThat(context).hasSingleBean(ExpiredSessionsDeletionJob.class));
    }

    @Test
    public void testEmailVerificationEnabledWithoutCallback() {
        this.contextRunner.withPropertyValues("sparkbit.security.email-verification.enabled=true")
                .run(context -> assertThat(context).hasFailed()
                );
    }

    @Test
    public void testEmailVerificationEnabled() {
        this.contextRunner.withUserConfiguration(CallbacksConfig.class)
                .withPropertyValues("sparkbit.security.email-verification.enabled=true")
                .run(context -> assertThat(context)
                        .hasSingleBean(EmailVerificationService.class)
                        .hasSingleBean(EmailVerificationController.class)
                );
    }

    @Test
    public void testExtraAuthnCheckEnabledWithoutCallback() {
        this.contextRunner.withPropertyValues("sparkbit.security.extra-authn-check.enabled=true")
                .run(context -> assertThat(context).hasFailed()
                );
    }

    @Test
    public void testExtraAuthnCheckEnabled() {
        this.contextRunner.withUserConfiguration(CallbacksConfig.class)
                .withPropertyValues("sparkbit.security.extra-authn-check.enabled=true")
                .run(context -> assertThat(context)
                        .hasSingleBean(ExtraAuthnCheckService.class)
                        .hasSingleBean(ExtraAuthnCheckController.class)
                );
    }

    @Test
    public void testPasswordResetEnabledWithoutCallback() {
        this.contextRunner.withPropertyValues("sparkbit.security.password-reset.enabled=true")
                .run(context -> assertThat(context).hasFailed()
                );
    }

    @Test
    public void testPasswordResetEnabled() {
        this.contextRunner.withUserConfiguration(CallbacksConfig.class)
                .withPropertyValues("sparkbit.security.password-reset.enabled=true")
                .run(context -> assertThat(context)
                        .hasSingleBean(PasswordResetService.class)
                        .hasSingleBean(PasswordResetController.class)
                );
    }

    public static class TestDataSourceConfig {
        @Bean
        public DataSource dataSource() {
            return new MysqlDataSource();
        }

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }

        @Bean
        public Clock clock() {
            return Clock.systemUTC();
        }

        @Bean
        public IdGenerator idGenerator() {
            return new IdGeneratorImpl();
        }

    }

    public static class CallbacksConfig {
        @Bean
        public PasswordResetChallengeCallback passwordResetChallengeCallback() {
            return challenge -> {
            };
        }

        @Bean
        public EmailVerificationChallengeCallback emailVerificationChallengeCallback() {
            return challenge -> {
            };
        }

        @Bean
        public ExtraAuthnCheckChallengeCallback extraAuthnCheckChallengeCallback() {
            return challenge -> {
            };
        }
    }
}
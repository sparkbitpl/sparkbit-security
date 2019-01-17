package pl.sparkbit.security.config;

import lombok.RequiredArgsConstructor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import pl.sparkbit.commons.util.IdGenerator;
import pl.sparkbit.security.Security;
import pl.sparkbit.security.callbacks.LoginResponseAdditionalDataCallback;
import pl.sparkbit.security.dao.CredentialsDao;
import pl.sparkbit.security.dao.SecurityChallengeDao;
import pl.sparkbit.security.dao.SessionDao;
import pl.sparkbit.security.dao.UserDetailsDao;
import pl.sparkbit.security.dao.impl.CredentialsDaoImpl;
import pl.sparkbit.security.dao.impl.SecurityChallengeDaoImpl;
import pl.sparkbit.security.dao.impl.SessionDaoImpl;
import pl.sparkbit.security.dao.impl.UserDetailsDaoImpl;
import pl.sparkbit.security.dao.mybatis.CredentialsMapper;
import pl.sparkbit.security.dao.mybatis.SecurityChallengeMapper;
import pl.sparkbit.security.dao.mybatis.SessionMapper;
import pl.sparkbit.security.dao.mybatis.UserDetailsMapper;
import pl.sparkbit.security.hooks.LoginHook;
import pl.sparkbit.security.hooks.LogoutHook;
import pl.sparkbit.security.login.LoginPrincipalFactory;
import pl.sparkbit.security.mvc.SecurityEndpointsRegistrations;
import pl.sparkbit.security.password.encoder.AuthTokenHasher;
import pl.sparkbit.security.password.encoder.AuthTokenHasherImpl;
import pl.sparkbit.security.password.encoder.PhpassPasswordEncoder;
import pl.sparkbit.security.password.policy.MinimalLengthPasswordPolicy;
import pl.sparkbit.security.password.policy.PasswordPolicy;
import pl.sparkbit.security.restauthn.AuthenticationTokenHelper;
import pl.sparkbit.security.service.ExtraAuthnCheckService;
import pl.sparkbit.security.service.SessionService;
import pl.sparkbit.security.service.UserDetailsService;
import pl.sparkbit.security.service.impl.SessionServiceImpl;
import pl.sparkbit.security.service.impl.UserDetailsServiceImpl;
import pl.sparkbit.security.util.SecureRandomStringGenerator;
import pl.sparkbit.security.util.SecureRandomStringGeneratorImpl;
import pl.sparkbit.security.util.SecurityChallenges;
import pl.sparkbit.security.util.SecurityChallengesImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Clock;

import static pl.sparkbit.security.config.SecurityProperties.DEFAULT_PASSWORD_POLICY_ENABLED;

@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(SecurityProperties.class)
@SuppressWarnings({"SpringFacetCodeInspection"})
@RequiredArgsConstructor
@MapperScan("pl.sparkbit.security.dao.mybatis")
@Import({SparkbitSecurityWebConfigurer.class, CorsConfigurer.class})
@ComponentScan({
        "pl.sparkbit.security.service.impl",
        "pl.sparkbit.security.mvc.controller",
        "pl.sparkbit.security.jobs"})
public class SparkbitSecurityAutoConfiguration {

    private final SecurityProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) ->
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }

    @Bean
    @ConditionalOnMissingBean
    public CredentialsDao credentialsDao(CredentialsMapper mapper) {
        return new CredentialsDaoImpl(mapper, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityChallengeDao securityChallengeDao(SecurityChallengeMapper mapper) {
        return new SecurityChallengeDaoImpl(mapper, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public SessionDao sessionDao(SessionMapper mapper) {
        return new SessionDaoImpl(mapper, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityChallenges securityChallenges(
            IdGenerator idGenerator,
            Clock clock,
            SecurityChallengeDao securityChallengeDao,
            SecureRandomStringGenerator secureRandomStringGenerator) {
        return new SecurityChallengesImpl(idGenerator, clock, securityChallengeDao,
                secureRandomStringGenerator, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public UserDetailsDao userDetailsDao(UserDetailsMapper mapper) {
        return new UserDetailsDaoImpl(mapper, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthTokenHasher authTokenHasher() {
        return new AuthTokenHasherImpl();
    }

    @Bean
    @ConditionalOnProperty(value = DEFAULT_PASSWORD_POLICY_ENABLED, havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public PasswordPolicy passwordPolicy() {
        return new MinimalLengthPasswordPolicy(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public LoginPrincipalFactory loginPrincipalFactory(SecurityProperties securityProperties) {
        return new LoginPrincipalFactory(securityProperties.getExpectedAuthnAttributes());
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityEndpointsRegistrations mvcEndpointsRegistrations() {
        return new SecurityEndpointsRegistrations(properties.getPaths());
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationTokenHelper authenticationTokenHelper() {
        return new AuthenticationTokenHelper(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        switch (properties.getPasswordEncoderType()) {
            case STANDARD:
                //noinspection deprecation
                return new StandardPasswordEncoder();
            case BCRYPT:
                return new BCryptPasswordEncoder();
            case PHPASS:
                return new PhpassPasswordEncoder();
        }
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnMissingBean
    public UserDetailsServiceImpl userDetailsService(UserDetailsDao userDetailsDao,
                                                     AuthTokenHasher authTokenHasher,
                                                     LoginPrincipalFactory loginPrincipalFactory) {
        return new UserDetailsServiceImpl(userDetailsDao, authTokenHasher, loginPrincipalFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    @ConditionalOnMissingBean
    public SessionService sessionService(
            SessionDao sessionDao,
            AuthTokenHasher authTokenHasher,
            Clock clock,
            Security security,
            SecureRandomStringGenerator secureRandomStringGenerator,
            SecurityProperties configuration,
            ObjectProvider<LoginHook> loginHook,
            ObjectProvider<LogoutHook> logoutHook,
            ObjectProvider<LoginResponseAdditionalDataCallback> additionalDataCallback,
            ObjectProvider<ExtraAuthnCheckService> extraAuthnCheckService) {
        return new SessionServiceImpl(sessionDao, authTokenHasher, clock, security,
                secureRandomStringGenerator, configuration, loginHook, logoutHook,
                additionalDataCallback, extraAuthnCheckService);
    }

    @Bean
    @ConditionalOnMissingBean
    public Security security() {
        return new Security();
    }

    @Bean
    @ConditionalOnMissingBean
    public SecureRandomStringGenerator secureRandomStringGenerator() {
        return new SecureRandomStringGeneratorImpl();
    }

}

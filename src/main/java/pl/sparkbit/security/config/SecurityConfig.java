package pl.sparkbit.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.GenericFilterBean;
import pl.sparkbit.security.hooks.LoginHook;
import pl.sparkbit.security.login.LoginAuthenticationFilter;
import pl.sparkbit.security.login.LoginPrincipalFactory;
import pl.sparkbit.security.login.SessionExpirationHeaderFilter;
import pl.sparkbit.security.login.social.FacebookAuthenticationProvider;
import pl.sparkbit.security.login.social.GoogleAuthenticationProvider;
import pl.sparkbit.security.login.social.TwitterAuthenticationProvider;
import pl.sparkbit.security.login.social.resolver.FacebookResolver;
import pl.sparkbit.security.login.social.resolver.GoogleResolver;
import pl.sparkbit.security.login.social.resolver.TwitterResolver;
import pl.sparkbit.security.password.encoder.PhpassPasswordEncoder;
import pl.sparkbit.security.restauthn.AuthenticationTokenHelper;
import pl.sparkbit.security.restauthn.RestAuthenticationFilter;
import pl.sparkbit.security.restauthn.user.UserAuthenticationProvider;
import pl.sparkbit.security.service.SessionService;
import pl.sparkbit.security.service.UserDetailsService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@MapperScan("pl.sparkbit.security.dao.mybatis")
@SuppressWarnings({"SpringFacetCodeInspection", "checkstyle:magicnumber"})
public class SecurityConfig {

    @Bean
    public ConversionService conversionService() {
        return new DefaultConversionService();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) ->
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }

    @Configuration
    @Order(1)
    @RequiredArgsConstructor
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static class LoginConfigurationAdapter extends WebSecurityConfigurerAdapter {

        private final AuthenticationEntryPoint authenticationEntryPoint;
        private final UserDetailsService userDetailsService;
        private final ObjectMapper objectMapper;
        private final LoginPrincipalFactory loginPrincipalFactory;
        private final Optional<FacebookResolver> facebookResolver;
        private final Optional<GoogleResolver> googleResolver;
        private final Optional<TwitterResolver> twitterResolver;
        private final Optional<LoginHook> loginHook;
        private final SecurityProperties configuration;

        @Bean
        public PasswordEncoder passwordEncoder() {
            switch (configuration.getPasswordEncoderType()) {
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
        public DaoAuthenticationProvider daoAuthenticationProvider() {
            DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
            daoAuthenticationProvider.setUserDetailsService(userDetailsService);
            daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
            return daoAuthenticationProvider;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            GenericFilterBean loginAuthenticationFiler =
                    new LoginAuthenticationFilter(authenticationManager(), authenticationEntryPoint,
                            loginPrincipalFactory, loginHook.orElseGet(() -> new LoginHook() {
                            })
                    );
            http
                    .cors().and()
                    .addFilterBefore(loginAuthenticationFiler, BasicAuthenticationFilter.class)
                    .antMatcher(configuration.getPaths().getLogin())

                    .sessionManagement().sessionCreationPolicy(STATELESS).and()
                    .anonymous().disable()
                    .logout().disable()
                    .rememberMe().disable()
                    .csrf().disable();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(daoAuthenticationProvider());

            // not lambda expression because GoogleAuthenticationProvider throws exceptions
            if (googleResolver.isPresent()) {
                auth.authenticationProvider(new GoogleAuthenticationProvider(googleResolver.get(), userDetailsService));
            }

            twitterResolver.ifPresent(tw ->
                    auth.authenticationProvider(new TwitterAuthenticationProvider(tw, userDetailsService,
                            objectMapper)));

            facebookResolver.ifPresent(fr ->
                    auth.authenticationProvider(new FacebookAuthenticationProvider(fr, userDetailsService,
                            objectMapper)));
        }
    }

    @Configuration
    @Order(2)
    @RequiredArgsConstructor
    public static class PublicRestConfigurationAdapter extends WebSecurityConfigurerAdapter {

        private final SecurityProperties configuration;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .cors().and()
                    .antMatcher(configuration.getPaths().getPublicPrefix() + "/**")
                    .sessionManagement().sessionCreationPolicy(STATELESS).and()
                    .anonymous().disable()
                    .logout().disable()
                    .rememberMe().disable()
                    .csrf().disable();
        }
    }

    @Configuration
    @Order(3)
    @RequiredArgsConstructor
    // With this configuration /error endpoint is publicly available and security context is not available
    // in error handling - even if user is authenticated.
    // If we needed SecurityContext to be populated, we'd have to add authentication here (but not access control).
    // The challenge would be to handle situations when errors are caused by problem with authentication (eg. invalid
    // session token). Such cases would cause errors during authentication for error handling as well and empty
    // response as a result.
    // Probably the best solution would be to have a completely different authentication configuration for /error than
    // for other rest endpoints (other filer, providers etc.).
    // See scs-64 in YouTrack
    public static class ErrorConfigurationAdapter extends WebSecurityConfigurerAdapter {

        private static final String ERROR_PATTERN = "/error/**";

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .cors().and()
                    .antMatcher(ERROR_PATTERN)

                    .sessionManagement().sessionCreationPolicy(STATELESS).and()
                    .anonymous().disable()
                    .logout().disable()
                    .rememberMe().disable()
                    .csrf().disable();
        }
    }

    @Configuration
    @RequiredArgsConstructor
    public static class RestConfigurationAdapter extends WebSecurityConfigurerAdapter {

        private final UserDetailsService userDetailsService;
        private final AuthenticationEntryPoint authenticationEntryPoint;
        private final AuthenticationTokenHelper authenticationTokenHelper;
        private final SessionService sessionService;
        private final SecurityProperties configuration;

        @Bean
        public UserAuthenticationProvider restAuthenticationProvider() {
            return new UserAuthenticationProvider(userDetailsService);
        }

        @SuppressWarnings({"ELValidationInJSP", "SpringElInspection"})
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            GenericFilterBean restAuthenticationFiler = new RestAuthenticationFilter(authenticationManager(),
                    authenticationEntryPoint, authenticationTokenHelper);

            SessionExpirationHeaderFilter sessionExpirationHeaderFilter = new SessionExpirationHeaderFilter(
                    sessionService,
                    configuration.getSessionExpiration().getTimestampHeaderName(),
                    authenticationTokenHelper);

            http
                    .cors().and()
                    .addFilterBefore(restAuthenticationFiler, BasicAuthenticationFilter.class)
                    .addFilterAfter(sessionExpirationHeaderFilter, RestAuthenticationFilter.class)
                    .authorizeRequests()
                    //fail-safe but LOGIN is already handled by LoginConfigurationAdapter
                    .antMatchers(configuration.getPaths().getLogin()).denyAll()
                    .antMatchers(configuration.getPaths().getExtraAuthCheck()).authenticated()
                    //below: block if user still has to perform extra authn check (eg. input code received by sms)
                    //Actuator other endpoints require special group
                    .requestMatchers(EndpointRequest.toAnyEndpoint())
                    .access("hasRole('ACTUATOR') and !principal.isExtraAuthnCheckRequired()")
                    .antMatchers(configuration.getPaths().getAdminPrefix() + "/**")
                    .access("hasRole('ADMIN') and !principal.isExtraAuthnCheckRequired()")
                    .anyRequest().access("!principal.isExtraAuthnCheckRequired()")
                    .and()

                    .sessionManagement().sessionCreationPolicy(STATELESS).and()
                    .anonymous().disable()
                    .logout().disable()
                    .rememberMe().disable()
                    .csrf().disable();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) {
            auth.authenticationProvider(restAuthenticationProvider());
        }

        /**
         * This method can be used when the application requires a special role for a particular antMatcher.
         * A special ConfigurationAdapter extending WebSecurityConfigurerAdapter must be implemented that will
         * call this method from within its configure(HttpSecurity http).
         * The @Order for this new ConfigurationAdapter must be after login/admin but before generic "rest" config
         * (so inclusive between 2 and 99).
         */
        @SuppressWarnings("unused")
        public void configure(HttpSecurity http, AuthenticationManager authenticationManager, String antMatcher,
                              String role) throws Exception {
            GenericFilterBean authenticationFilter = new RestAuthenticationFilter(authenticationManager,
                    authenticationEntryPoint, authenticationTokenHelper);

            http
                    .antMatcher(antMatcher)
                    .addFilterBefore(authenticationFilter, BasicAuthenticationFilter.class)
                    .authorizeRequests()
                    .anyRequest().hasRole(role)
                    .and()

                    .sessionManagement().sessionCreationPolicy(STATELESS).and()
                    .anonymous().disable()
                    .logout().disable()
                    .rememberMe().disable()
                    .csrf().disable();
        }
    }
}

package pl.sparkbit.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.Http401AuthenticationEntryPoint;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.GenericFilterBean;
import pl.sparkbit.security.login.LoginAuthenticationFilter;
import pl.sparkbit.security.login.social.FacebookAuthenticationProvider;
import pl.sparkbit.security.login.social.GoogleAuthenticationProvider;
import pl.sparkbit.security.login.social.TwitterAuthenticationProvider;
import pl.sparkbit.security.login.social.resolver.FacebookResolver;
import pl.sparkbit.security.login.social.resolver.GoogleResolver;
import pl.sparkbit.security.login.social.resolver.TwitterResolver;
import pl.sparkbit.security.password.encoder.PasswordEncoderType;
import pl.sparkbit.security.password.encoder.PhpassPasswordEncoder;
import pl.sparkbit.security.restauthn.AuthenticationTokenHelper;
import pl.sparkbit.security.restauthn.RestAuthenticationFilter;
import pl.sparkbit.security.restauthn.user.UserAuthenticationProvider;
import pl.sparkbit.security.service.UserDetailsService;

import java.util.Arrays;
import java.util.Optional;

import static java.util.stream.Collectors.toSet;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static pl.sparkbit.security.config.Properties.PASSWORD_ENCODER_TYPE;
import static pl.sparkbit.security.mvc.controller.Paths.LOGIN;

@Configuration
@EnableWebSecurity
@MapperScan("pl.sparkbit.security.dao.mybatis")
@SuppressWarnings("SpringFacetCodeInspection")
public class SecurityConfig {

    @Bean
    public ConversionService conversionService() {
        return new DefaultConversionService();
    }

    @Bean
    public CorsConfigurer corsConfigurer() {
        return new CorsConfigurer();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new Http401AuthenticationEntryPoint(null);
    }

    @Configuration
    @Order(1)
    @RequiredArgsConstructor
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static class LoginConfigurationAdapter extends WebSecurityConfigurerAdapter {

        private final AuthenticationEntryPoint authenticationEntryPoint;
        private final UserDetailsService userDetailsService;
        private final ObjectMapper objectMapper;
        private final Optional<FacebookResolver> facebookResolver;
        private final Optional<GoogleResolver> googleResolver;
        private final Optional<TwitterResolver> twitterResolver;

        @Value("${" + Properties.EXPECTED_AUTHN_ATTRIBUTES + "}")
        private String[] expectedAuthnAttributes;
        @Value("${" + PASSWORD_ENCODER_TYPE + ":BCRYPT}")
        private PasswordEncoderType passwordEncoderType;

        @Bean
        public PasswordEncoder passwordEncoder() {
            switch (passwordEncoderType) {
                case STANDARD:
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
                            Arrays.stream(expectedAuthnAttributes).collect(toSet()));
            http
                    .cors().and()
                    .addFilterBefore(loginAuthenticationFiler, BasicAuthenticationFilter.class)
                    .antMatcher(LOGIN)

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
    public static class PublicRestConfigurationAdapter extends WebSecurityConfigurerAdapter {

        private static final String PUBLIC_PATTERN = "/public/**";

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher(PUBLIC_PATTERN)
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

        private static final String ADMIN_PATTERN = "/admin/**";

        private final UserDetailsService userDetailsService;
        private final AuthenticationEntryPoint authenticationEntryPoint;
        private final AuthenticationTokenHelper authenticationTokenHelper;

        @Bean
        public UserAuthenticationProvider restAuthenticationProvider() {
            return new UserAuthenticationProvider(userDetailsService);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            GenericFilterBean restAuthenticationFiler = new RestAuthenticationFilter(authenticationManager(),
                    authenticationEntryPoint, authenticationTokenHelper);

            http
                    .cors().and()
                    .addFilterBefore(restAuthenticationFiler, BasicAuthenticationFilter.class)

                    .authorizeRequests()
                    //fail-safe but LOGIN is already handled by LoginConfigurationAdapter
                    .antMatchers(LOGIN).denyAll()
                    .antMatchers(ADMIN_PATTERN).hasRole("ADMIN")
                    .anyRequest().authenticated()
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
         * (so between 1 and 100).
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
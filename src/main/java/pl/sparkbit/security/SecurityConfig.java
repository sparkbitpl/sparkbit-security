package pl.sparkbit.security;

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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.GenericFilterBean;
import pl.sparkbit.security.password.encoder.PasswordEncoderType;
import pl.sparkbit.security.password.encoder.PhpassPasswordEncoder;
import pl.sparkbit.security.rest.authn.AuthenticationTokenHelper;
import pl.sparkbit.security.rest.authn.RestAuthenticationFilter;
import pl.sparkbit.security.rest.authn.user.UserAuthenticationProvider;
import pl.sparkbit.security.rest.service.RestSecurityService;
import pl.sparkbit.security.session.auth.LoginAuthenticationFilter;
import pl.sparkbit.security.session.auth.social.FacebookAuthenticationProvider;
import pl.sparkbit.security.session.auth.social.GoogleAuthenticationProvider;
import pl.sparkbit.security.session.auth.social.TwitterAuthenticationProvider;
import pl.sparkbit.security.session.auth.social.resolver.FacebookResolver;
import pl.sparkbit.security.session.auth.social.resolver.GoogleResolver;
import pl.sparkbit.security.session.auth.social.resolver.TwitterResolver;
import pl.sparkbit.security.session.service.SessionService;

import java.util.Arrays;
import java.util.Optional;

import static java.util.stream.Collectors.toSet;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static pl.sparkbit.security.Paths.LOGIN;

@Configuration
@EnableWebSecurity
@MapperScan({"pl.sparkbit.security.rest.dao.mybatis", "pl.sparkbit.security.session.dao.mybatis",
        "pl.sparkbit.security.challenge.dao.mybatis"})
@SuppressWarnings("SpringFacetCodeInspection")
public class SecurityConfig {

    @Bean
    public ConversionService conversionService() {
        return new DefaultConversionService();
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

        private final SessionService sessionService;
        private final AuthenticationEntryPoint authenticationEntryPoint;
        private final UserDetailsService userDetailsService;
        private final ObjectMapper objectMapper;
        private final Optional<FacebookResolver> facebookResolver;
        private final Optional<GoogleResolver> googleResolver;
        private final Optional<TwitterResolver> twitterResolver;

        @Value("${sparkbit.security.expected-authn-attributes}")
        private String[] expectedAuthnAttributes;
        @Value("${sparkbit.security.passwordEncoderType:BCRYPT}")
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
            daoAuthenticationProvider.setUserDetailsService(sessionService);
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

        private final RestSecurityService restSecurityService;
        private final AuthenticationEntryPoint authenticationEntryPoint;
        private final AuthenticationTokenHelper authenticationTokenHelper;

        @Bean
        public UserAuthenticationProvider restAuthenticationProvider() {
            return new UserAuthenticationProvider(restSecurityService);
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

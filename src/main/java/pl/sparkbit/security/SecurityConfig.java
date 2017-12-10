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
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.GenericFilterBean;
import pl.sparkbit.security.login.LoginAuthenticationFilter;
import pl.sparkbit.security.password.PasswordEncoderType;
import pl.sparkbit.security.password.PhpassPasswordEncoder;
import pl.sparkbit.security.rest.RestAuthenticationFilter;
import pl.sparkbit.security.rest.RestAuthenticationProvider;
import pl.sparkbit.security.social.FacebookAuthenticationProvider;
import pl.sparkbit.security.social.GoogleAuthenticationProvider;
import pl.sparkbit.security.social.TwitterAuthenticationProvider;
import pl.sparkbit.security.social.resolver.FacebookResolver;
import pl.sparkbit.security.social.resolver.GoogleResolver;
import pl.sparkbit.security.social.resolver.TwitterResolver;

import java.util.Arrays;
import java.util.Optional;

import static java.util.stream.Collectors.toSet;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static pl.sparkbit.security.login.LoginController.LOGIN;

@Configuration
@EnableWebSecurity
@MapperScan("pl.sparkbit.security.dao.mybatis")
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
    public static class LoginConfigurationAdapter extends WebSecurityConfigurerAdapter {

        private final SecurityService securityService;
        private final AuthenticationEntryPoint authenticationEntryPoint;
        private final UserDetailsService userDetailsService;
        private final ObjectMapper objectMapper;
        private final Optional<FacebookResolver> facebookResolver;
        private final Optional<GoogleResolver> googleResolver;
        private final Optional<TwitterResolver> twitterResolver;

        @Value("${sparkbit.security.expected-authn-attributes}")
        private String[] expectedAuthnAttributes;
        @Value("${sparkbit.security.passwordEncoderType:DEFAULT}")
        private PasswordEncoderType passwordEncoderType;

        @Bean
        public PasswordEncoder passwordEncoder() {
            switch (passwordEncoderType) {
                case PHPASS:
                    return new PhpassPasswordEncoder();
            }
            return new StandardPasswordEncoder();
        }

        @Bean
        public DaoAuthenticationProvider daoAuthenticationProvider() {
            DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
            daoAuthenticationProvider.setUserDetailsService(securityService);
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

        private final SecurityService securityService;
        private final AuthenticationEntryPoint authenticationEntryPoint;

        @Bean
        public RestAuthenticationProvider restAuthenticationProvider() {
            return new RestAuthenticationProvider(securityService);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            GenericFilterBean restAuthenticationFiler =
                    new RestAuthenticationFilter(authenticationManager(), authenticationEntryPoint);

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
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(restAuthenticationProvider());
        }
    }
}

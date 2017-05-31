package pl.sparkbit.security;

import lombok.RequiredArgsConstructor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.Http401AuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.GenericFilterBean;
import pl.sparkbit.security.login.LoginAuthenticationFilter;
import pl.sparkbit.security.rest.RestAuthenticationFilter;
import pl.sparkbit.security.rest.RestAuthenticationProvider;

import java.util.Arrays;

import static java.util.stream.Collectors.toSet;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static pl.sparkbit.security.login.LoginController.LOGIN;

@Configuration
@EnableWebSecurity
@MapperScan("pl.sparkbit.security.dao.mybatis")
public class SecurityConfig {

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

        @Value("${sparkbit.security.expected-authn-attributes}")
        private String[] expectedAuthnAttributes;

        @Bean
        public PasswordEncoder passwordEncoder() {
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
                    .addFilterBefore(loginAuthenticationFiler, BasicAuthenticationFilter.class)
                    .antMatcher(LOGIN)

                    .sessionManagement().sessionCreationPolicy(STATELESS).and()
                    .anonymous().disable()
                    .logout().disable()
                    .rememberMe().disable()
                    .csrf().disable();
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

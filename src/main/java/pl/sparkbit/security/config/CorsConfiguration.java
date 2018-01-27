package pl.sparkbit.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import static pl.sparkbit.security.config.Properties.*;

@Configuration
@SuppressWarnings("SpringFacetCodeInspection")
public class CorsConfiguration {

    private static final boolean DEFAULT_ALLOW_CREDENTIALS = true;
    private static final String DEFAULT_ALLOWED_HEADERS = "*";
    private static final String DEFAULT_ALLOWED_METHODS = "GET, POST, PUT, DELETE, HEAD";
    private static final String DEFAULT_ALLOWED_ORIGINS = "*";
    private static final long DEFAULT_MAX_AGE_SECONDS = 1800;

    @Value("${" + CORS_ALLOW_CREDENTIALS + ":" + DEFAULT_ALLOW_CREDENTIALS + "}")
    private boolean allowCredentials;
    @Value("${" + CORS_ALLOWED_HEADERS + ":" + DEFAULT_ALLOWED_HEADERS + "}")
    private String[] allowedHeaders;
    @Value("${" + CORS_ALLOWED_METHODS + ":" + DEFAULT_ALLOWED_METHODS + "}")
    private String[] allowedMethods;
    @Value("${" + CORS_ALLOWED_ORIGINS + ":" + DEFAULT_ALLOWED_ORIGINS + "}")
    private String[] allowedOrigins;
    @Value("${" + CORS_MAX_AGE_SECONDS + ":" + DEFAULT_MAX_AGE_SECONDS + "}")
    private long maxAgeSeconds;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new CorsConfigurer(allowCredentials, allowedHeaders, allowedMethods, allowedOrigins, maxAgeSeconds);
    }

    @RequiredArgsConstructor
    static class CorsConfigurer extends WebMvcConfigurerAdapter {

        private final boolean allowCredentials;
        private final String[] allowedHeaders;
        private final String[] allowedMethods;
        private final String[] allowedOrigins;
        private final long maxAgeSeconds;

        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowCredentials(allowCredentials)
                    .allowedHeaders(allowedHeaders)
                    .allowedMethods(allowedMethods)
                    .allowedOrigins(allowedOrigins)
                    .maxAge(maxAgeSeconds);
        }
    }
}

package pl.sparkbit.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import static pl.sparkbit.security.Security.DEFAULT_SESSION_EXPIRES_AT_HEADER_NAME;
import static pl.sparkbit.security.config.Properties.*;

public class CorsConfigurer extends WebMvcConfigurerAdapter {

    private static final boolean DEFAULT_ALLOW_CREDENTIALS = true;
    private static final String DEFAULT_ALLOWED_HEADERS = "*";
    private static final String DEFAULT_ALLOWED_METHODS = "GET, POST, PUT, DELETE, HEAD";
    private static final String DEFAULT_ALLOWED_ORIGINS = "*";
    private static final String DEFAULT_EXPOSED_HEADERS = DEFAULT_SESSION_EXPIRES_AT_HEADER_NAME;
    private static final long DEFAULT_MAX_AGE_SECONDS = 1800;

    @Value("${" + CORS_ALLOW_CREDENTIALS + ":" + DEFAULT_ALLOW_CREDENTIALS + "}")
    private boolean allowCredentials;
    @Value("${" + CORS_ALLOWED_HEADERS + ":" + DEFAULT_ALLOWED_HEADERS + "}")
    private String[] allowedHeaders;
    @Value("${" + CORS_EXPOSED_HEADERS + ":" + DEFAULT_EXPOSED_HEADERS + "}")
    private String[] exposedHeaders;
    @Value("${" + CORS_ALLOWED_METHODS + ":" + DEFAULT_ALLOWED_METHODS + "}")
    private String[] allowedMethods;
    @Value("${" + CORS_ALLOWED_ORIGINS + ":" + DEFAULT_ALLOWED_ORIGINS + "}")
    private String[] allowedOrigins;
    @Value("${" + CORS_MAX_AGE_SECONDS + ":" + DEFAULT_MAX_AGE_SECONDS + "}")
    private long maxAgeSeconds;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(allowCredentials)
                .allowedHeaders(allowedHeaders)
                .exposedHeaders(exposedHeaders)
                .allowedMethods(allowedMethods)
                .allowedOrigins(allowedOrigins)
                .maxAge(maxAgeSeconds);
    }
}

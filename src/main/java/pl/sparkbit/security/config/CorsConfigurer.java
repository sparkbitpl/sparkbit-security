package pl.sparkbit.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class CorsConfigurer implements WebMvcConfigurer {

    private final SecurityProperties configuration;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(configuration.getCors().getAllowCredentials())
                .allowedHeaders(configuration.getCors().getAllowedHeaders())
                .exposedHeaders(configuration.getCors().getExposedHeaders())
                .allowedMethods(configuration.getCors().getAllowedMethods())
                .allowedOrigins(configuration.getCors().getAllowedOrigins())
                .maxAge(configuration.getCors().getMaxAge().getSeconds());
    }
}

package pl.sparkbit.security.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import pl.sparkbit.security.config.SecurityProperties;

@Configuration
@RequiredArgsConstructor
public class MvcConfiguration {

    private final SecurityProperties configuration;

    @Bean
    protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
        return new EndpointMappings(configuration.getPaths());
    }
}

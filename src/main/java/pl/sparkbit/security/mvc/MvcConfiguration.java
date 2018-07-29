package pl.sparkbit.security.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import pl.sparkbit.security.config.SecurityProperties;

@Configuration
@RequiredArgsConstructor
public class MvcConfiguration extends WebMvcConfigurationSupport {

    private final SecurityProperties configuration;

    @Override
    protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
        return new EndpointMappings(configuration.getPaths());
    }
}

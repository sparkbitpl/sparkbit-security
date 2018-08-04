package pl.sparkbit.security.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import pl.sparkbit.security.config.SecurityProperties;

@Component
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class MvcConfiguration implements WebMvcRegistrations {

    private final SecurityProperties configuration;

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new EndpointMappings(configuration.getPaths());
    }
}

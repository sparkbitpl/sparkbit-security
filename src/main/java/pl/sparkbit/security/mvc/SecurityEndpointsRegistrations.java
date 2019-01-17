package pl.sparkbit.security.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import pl.sparkbit.security.config.SecurityProperties;

@RequiredArgsConstructor
public class SecurityEndpointsRegistrations implements WebMvcRegistrations {

    private final SecurityProperties.Paths properties;

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new SecurityHandlerMapping(properties);
    }
}

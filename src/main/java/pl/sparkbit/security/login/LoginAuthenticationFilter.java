package pl.sparkbit.security.login;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;
import pl.sparkbit.security.hooks.LoginHook;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class LoginAuthenticationFilter extends GenericFilterBean {

    private final AuthenticationManager authenticationManager;
    private final AuthenticationEntryPoint entryPoint;
    private final LoginPrincipalFactory loginPrincipalFactory;
    private final LoginHook loginHook;
    private final ObjectReader jsonReader;

    public LoginAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationEntryPoint entryPoint,
                                     LoginPrincipalFactory loginPrincipalFactory, LoginHook loginHook) {
        this.authenticationManager = authenticationManager;
        this.entryPoint = entryPoint;
        this.loginPrincipalFactory = loginPrincipalFactory;
        this.loginHook = loginHook;
        LoginDTODeserializer deserializer = new LoginDTODeserializer();
        SimpleModule module = new SimpleModule("LoginDeserializerModule", Version.unknownVersion());
        module.addDeserializer(LoginDTO.class, deserializer);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);
        this.jsonReader = objectMapper.readerFor(LoginDTO.class);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        try {
            LoginDTO dto = getLoginData(request.getReader());
            LoginPrincipal loginPrincipal = loginPrincipalFactory.generate(dto.getAuthnAttributesMap());
            Authentication token = dto.toToken(loginPrincipal);

            Authentication authentication = authenticationManager.authenticate(token);
            Assert.isTrue(authentication.isAuthenticated(),
                    "Authentication is not authenticated after successful authentication");

            String userId = ((LoginUserDetails) authentication.getPrincipal()).getUserId();
            loginHook.performAdditionalAuthenticationChecks(userId, loginPrincipal.getAuthnAttributes(), request);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            loginHook.processAdditionalData(userId, dto.getAdditionalData(), request);
        } catch (AuthenticationException failed) {
            SecurityContextHolder.clearContext();
            entryPoint.commence(request, response, failed);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private LoginDTO getLoginData(Reader reader) throws AuthenticationException, IOException {
        try {
            return this.jsonReader.readValue(reader);
        } catch (JsonProcessingException e) {
            throw new InvalidJsonAuthenticationException("Invalid request JSON", e);
        }
    }
}

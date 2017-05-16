package pl.sparkbit.security.login;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
public class LoginAuthenticationFilter extends GenericFilterBean {

    private final AuthenticationManager authenticationManager;
    private final AuthenticationEntryPoint entryPoint;
    private final Set<String> expectedAuthnAttributes;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        try {
            LoginDTO dto = getLoginData(request.getReader());
            AuthnAttributes authnAttributes =
                    new AuthnAttributes(dto.getAuthnAttributes(), expectedAuthnAttributes);
            LoginPrincipal principal = new LoginPrincipal(authnAttributes);
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal,
                    dto.getPassword());

            Authentication authentication = authenticationManager.authenticate(token);
            Assert.isTrue(authentication.isAuthenticated(),
                    "Authentication is not authenticated after successful authentication");
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException failed) {
            SecurityContextHolder.clearContext();
            entryPoint.commence(request, response, failed);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private LoginDTO getLoginData(Reader reader) throws AuthenticationException, IOException {
        try {
            return new ObjectMapper().readerFor(LoginDTO.class).readValue(reader);
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException("Invalid request JSON", e);
        }
    }
}

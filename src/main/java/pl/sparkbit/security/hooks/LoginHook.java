package pl.sparkbit.security.hooks;

import org.springframework.security.core.AuthenticationException;
import pl.sparkbit.security.login.AuthnAttributes;

import javax.servlet.http.HttpServletRequest;

@SuppressWarnings("unused")
public interface LoginHook {

    /**
     * This method must throw AuthenticationException if additional checks fail.
     */
    default void performAdditionalAuthenticationChecks(String userId, AuthnAttributes authnAttributes,
                                                       HttpServletRequest request)
            throws AuthenticationException {
    }

    default void doAfterSuccessfulLogin(String userId) {
    }
}

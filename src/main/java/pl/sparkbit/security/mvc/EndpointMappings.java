package pl.sparkbit.security.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import pl.sparkbit.commons.buildinfo.mvc.controller.BuildInfoController;
import pl.sparkbit.security.config.SecurityProperties;
import pl.sparkbit.security.mvc.controller.*;
import pl.sparkbit.security.mvc.dto.in.ChangePasswordDTO;
import pl.sparkbit.security.mvc.dto.in.ExtraAuthnCheckDTO;
import pl.sparkbit.security.mvc.dto.in.ResetPasswordDTO;
import pl.sparkbit.security.mvc.dto.in.VerifyEmailDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RequiredArgsConstructor
public class EndpointMappings extends RequestMappingHandlerMapping {

    private static final Method LOGIN_METHOD;
    private static final Method LOGOUT_METHOD;
    private static final Method PERFORM_EXTRA_AUTH_CHECK_METHOD;
    private static final Method CHANGE_PASSWORD_METHOD;
    private static final Method INITIATE_PASSWORD_RESET_METHOD;
    private static final Method RESET_PASSWORD_METHOD;
    private static final Method VERIFY_EMAIL_METHOD;
    private static final Method BUILD_INFO_GET_METHOD;

    static {
        try {
            LOGIN_METHOD =
                    SessionController.class.getMethod("login", HttpServletRequest.class, HttpServletResponse.class);
            LOGOUT_METHOD = SessionController.class.getMethod("logout", HttpServletResponse.class);
            PERFORM_EXTRA_AUTH_CHECK_METHOD =
                    ExtraAuthnCheckController.class.getMethod("performExtraAuthnCheck", ExtraAuthnCheckDTO.class);
            CHANGE_PASSWORD_METHOD =
                    PasswordChangeController.class.getMethod("changePassword", ChangePasswordDTO.class);
            INITIATE_PASSWORD_RESET_METHOD =
                    PasswordResetController.class.getMethod("initiatePasswordReset", Map.class);
            RESET_PASSWORD_METHOD = PasswordResetController.class.getMethod("resetPassword", ResetPasswordDTO.class);
            VERIFY_EMAIL_METHOD = EmailVerificationController.class.getMethod("verifyEmail", VerifyEmailDTO.class);
            BUILD_INFO_GET_METHOD = BuildInfoController.class.getMethod("getBuildInfo");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final SecurityProperties.Paths paths;

    @Override
    @SuppressWarnings("checkstyle:NPathComplexity")
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        // If new controller methods are added to sparkbit-commons or sparkbit-security they should be mapped here
        if (method.equals(LOGIN_METHOD)) {
            return requestMappingInfo(paths.getLogin(), POST);
        }
        if (method.equals(LOGOUT_METHOD)) {
            return requestMappingInfo(paths.getLogout(), POST);
        }
        if (method.equals(PERFORM_EXTRA_AUTH_CHECK_METHOD)) {
            return requestMappingInfo(paths.getExtraAuthCheck(), POST);
        }
        if (method.equals(CHANGE_PASSWORD_METHOD)) {
            return requestMappingInfo(paths.getPassword(), PUT);
        }
        if (method.equals(INITIATE_PASSWORD_RESET_METHOD)) {
            return requestMappingInfo(paths.getPublicPasswordResetToken(), POST);
        }
        if (method.equals(RESET_PASSWORD_METHOD)) {
            return requestMappingInfo(paths.getPublicPassword(), POST);
        }
        if (method.equals(VERIFY_EMAIL_METHOD)) {
            return requestMappingInfo(paths.getPublicEmail(), POST);
        }
        if (method.equals(BUILD_INFO_GET_METHOD)) {
            return requestMappingInfo(paths.getBuildInfo(), GET);
        }
        return super.getMappingForMethod(method, handlerType);
    }

    private RequestMappingInfo requestMappingInfo(String path, RequestMethod method) {
        return RequestMappingInfo.paths(path).methods(method).build();
    }
}

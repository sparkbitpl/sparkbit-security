package pl.sparkbit.security.mvc.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.sparkbit.security.config.Properties;
import pl.sparkbit.security.login.AuthnAttributes;
import pl.sparkbit.security.mvc.dto.in.ResetPasswordDTO;
import pl.sparkbit.security.service.PasswordResetService;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static pl.sparkbit.security.config.Properties.PASSWORD_RESET_ENABLED;
import static pl.sparkbit.security.mvc.controller.Paths.PUBLIC_PASSWORD;
import static pl.sparkbit.security.mvc.controller.Paths.PUBLIC_PASSWORD_RESET_TOKEN;

@ConditionalOnProperty(value = PASSWORD_RESET_ENABLED, havingValue = "true")
@RestController
@SuppressWarnings("unused")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;
    private final Set<String> expectedAttributes;

    public PasswordResetController(PasswordResetService passwordResetService,
                                   @Value("${" + Properties.EXPECTED_AUTHN_ATTRIBUTES + "}")
                                           String[] expectedAuthnAttributes) {
        this.passwordResetService = passwordResetService;
        this.expectedAttributes = Arrays.stream(expectedAuthnAttributes).collect(toSet());
    }

    @PostMapping(PUBLIC_PASSWORD_RESET_TOKEN)
    @ResponseStatus(NO_CONTENT)
    public void initiatePasswordReset(@RequestBody Map<String, String> requestBody) {
        passwordResetService.initiatePasswordReset(new AuthnAttributes(requestBody, expectedAttributes));
    }

    @PostMapping(PUBLIC_PASSWORD)
    @ResponseStatus(NO_CONTENT)
    public void resetPassword(@RequestBody @Valid ResetPasswordDTO dto) {
        passwordResetService.resetPassword(dto.getToken(), dto.getPassword());
    }
}

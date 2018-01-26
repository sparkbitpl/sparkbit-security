package pl.sparkbit.security.mvc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.sparkbit.security.mvc.dto.in.InitiatePasswordResetDTO;
import pl.sparkbit.security.mvc.dto.in.ResetPasswordDTO;
import pl.sparkbit.security.service.PasswordResetService;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static pl.sparkbit.security.config.Properties.PASSWORD_RESET_ENABLED;
import static pl.sparkbit.security.mvc.controller.Paths.PUBLIC_PASSWORD;
import static pl.sparkbit.security.mvc.controller.Paths.PUBLIC_PASSWORD_RESET_TOKEN;

@ConditionalOnProperty(value = PASSWORD_RESET_ENABLED, havingValue = "true")
@RequiredArgsConstructor
@RestController
@SuppressWarnings("unused")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping(PUBLIC_PASSWORD_RESET_TOKEN)
    @ResponseStatus(NO_CONTENT)
    public void initiatePasswordReset(@RequestBody @Valid InitiatePasswordResetDTO dto) {
        passwordResetService.initiatePasswordReset(dto.getEmail());
    }

    @PostMapping(PUBLIC_PASSWORD)
    @ResponseStatus(NO_CONTENT)
    public void resetPassword(@RequestBody @Valid ResetPasswordDTO dto) {
        passwordResetService.resetPassword(dto.getToken(), dto.getPassword());
    }
}

package pl.sparkbit.security.mvc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.sparkbit.security.mvc.dto.in.ChangePasswordDTO;
import pl.sparkbit.security.service.PasswordChangeService;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static pl.sparkbit.security.config.Properties.PASSWORD_CHANGE_ENABLED;
import static pl.sparkbit.security.mvc.controller.Paths.PASSWORD;

@ConditionalOnProperty(value = PASSWORD_CHANGE_ENABLED, havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@RestController
@SuppressWarnings("unused")
public class PasswordChangeController {

    private final PasswordChangeService passwordChangeService;

    @PutMapping(PASSWORD)
    @ResponseStatus(NO_CONTENT)
    public void changePassword(@RequestBody @Valid ChangePasswordDTO dto) {
        passwordChangeService.changeCurrentUserPassword(dto);
    }
}

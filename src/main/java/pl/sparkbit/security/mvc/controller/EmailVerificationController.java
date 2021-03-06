package pl.sparkbit.security.mvc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.sparkbit.security.mvc.dto.in.VerifyEmailDTO;
import pl.sparkbit.security.service.EmailVerificationService;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static pl.sparkbit.security.config.SecurityProperties.EMAIL_VERIFICATION_ENABLED;

@ConditionalOnProperty(value = EMAIL_VERIFICATION_ENABLED, havingValue = "true")
@RequiredArgsConstructor
@RestController
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @ResponseStatus(NO_CONTENT)
    public void verifyEmail(@RequestBody @Valid VerifyEmailDTO dto) {
        emailVerificationService.verifyEmail(dto);
    }
}

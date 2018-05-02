package pl.sparkbit.security.mvc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.sparkbit.security.mvc.dto.in.ExtraAuthnCheckDTO;
import pl.sparkbit.security.service.ExtraAuthnCheckService;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static pl.sparkbit.security.config.SecurityProperties.EXTRA_AUTHENTICATION_CHECK_ENABLED;
import static pl.sparkbit.security.mvc.controller.Paths.EXTRA_AUTH_CHECK;

@ConditionalOnProperty(value = EXTRA_AUTHENTICATION_CHECK_ENABLED, havingValue = "true")
@RequiredArgsConstructor
@RestController
@SuppressWarnings("unused")
public class ExtraAuthnCheckController {

    private final ExtraAuthnCheckService extraAuthnCheckService;

    @PostMapping(EXTRA_AUTH_CHECK)
    @ResponseStatus(NO_CONTENT)
    public void performExtraAuthnCheck(@RequestBody @Valid ExtraAuthnCheckDTO dto) {
        extraAuthnCheckService.performExtraAuthnCheck(dto);
    }
}

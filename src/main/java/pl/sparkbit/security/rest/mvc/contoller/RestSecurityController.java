package pl.sparkbit.security.rest.mvc.contoller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.sparkbit.security.rest.mvc.dto.in.ChangePasswordDTO;
import pl.sparkbit.security.rest.service.RestSecurityService;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static pl.sparkbit.security.Paths.PASSWORD;
import static pl.sparkbit.security.Properties.STANDARD_ENDPOINTS_ENABLED;

@ConditionalOnProperty(value = STANDARD_ENDPOINTS_ENABLED, havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@RestController
@SuppressWarnings("unused")
public class RestSecurityController {

    private final RestSecurityService restSecurityService;

    @PutMapping(PASSWORD)
    @ResponseStatus(NO_CONTENT)
    public void changePassword(@RequestBody @Valid ChangePasswordDTO dto) {
        restSecurityService.changeCurrentUserPassword(dto);
    }
}

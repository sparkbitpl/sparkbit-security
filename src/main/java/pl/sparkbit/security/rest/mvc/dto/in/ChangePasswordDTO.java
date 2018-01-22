package pl.sparkbit.security.rest.mvc.dto.in;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import pl.sparkbit.security.password.policy.Password;

import javax.validation.constraints.NotNull;

@Data
public class ChangePasswordDTO {

    @NotEmpty
    private final String currentPassword;

    @NotNull
    @Password
    private final String newPassword;
}

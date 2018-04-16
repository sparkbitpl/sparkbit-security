package pl.sparkbit.security.mvc.dto.in;

import lombok.Data;
import pl.sparkbit.security.password.policy.Password;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ChangePasswordDTO {

    @NotEmpty
    private final String currentPassword;

    @NotNull
    @Password
    private final String newPassword;
}

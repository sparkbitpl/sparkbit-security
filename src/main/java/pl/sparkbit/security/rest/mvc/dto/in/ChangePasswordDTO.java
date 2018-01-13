package pl.sparkbit.security.rest.mvc.dto.in;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Data
public class ChangePasswordDTO {

    private static final int MINIMAL_PASSWORD_LENGTH = 8;

    @NotEmpty
    private final String currentPassword;

    @NotNull
    @Length(min = MINIMAL_PASSWORD_LENGTH)
    private final String newPassword;
}

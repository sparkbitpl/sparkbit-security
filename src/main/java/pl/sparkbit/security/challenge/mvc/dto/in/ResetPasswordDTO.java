package pl.sparkbit.security.challenge.mvc.dto.in;

import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class ResetPasswordDTO {

    private static final int MINIMAL_PASSWORD_LENGTH = 8;

    @NotEmpty
    private final String token;

    @NonNull
    @Length(min = MINIMAL_PASSWORD_LENGTH)
    private final String password;
}

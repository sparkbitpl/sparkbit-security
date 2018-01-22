package pl.sparkbit.security.challenge.mvc.dto.in;

import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.NotEmpty;
import pl.sparkbit.security.password.policy.Password;

@Data
public class ResetPasswordDTO {

    @NotEmpty
    private final String token;

    @NonNull
    @Password
    private final String password;
}

package pl.sparkbit.security.mvc.dto.in;

import lombok.Data;
import lombok.NonNull;
import pl.sparkbit.security.domain.SecurityChallengeType;
import pl.sparkbit.security.password.policy.Password;

import javax.validation.constraints.NotEmpty;

@Data
public class ResetPasswordDTO {

    @NotEmpty
    private final String token;

    @NonNull
    @Password
    private final String password;

    private final SecurityChallengeType resetType;
}

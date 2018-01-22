package pl.sparkbit.security.challenge.mvc.dto.in;

import lombok.Data;
import org.hibernate.validator.constraints.Email;

@Data
public class InitiatePasswordResetDTO {

    @Email
    private final String email;
}

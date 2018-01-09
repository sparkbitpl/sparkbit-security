package pl.sparkbit.security.challenge.mvc.dto.in;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class VerifyEmailDTO {

    @NotEmpty
    private final String token;
}

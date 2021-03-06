package pl.sparkbit.security.mvc.dto.in;

import lombok.Data;
import javax.validation.constraints.NotEmpty;

@Data
public class VerifyEmailDTO {

    @NotEmpty
    private final String token;
}

package pl.sparkbit.security.mvc.dto.in;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class ExtraAuthnCheckDTO {

    @NotEmpty
    private final String token;
}

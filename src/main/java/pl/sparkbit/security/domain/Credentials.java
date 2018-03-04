package pl.sparkbit.security.domain;

import lombok.Builder;
import lombok.Data;

@Builder(toBuilder = true)
@Data
public class Credentials {

    private final String userId;
    private final String password;
    private final Boolean enabled;
    private final Boolean deleted;
}

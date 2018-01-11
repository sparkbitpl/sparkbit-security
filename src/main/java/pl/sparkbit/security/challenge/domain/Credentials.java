package pl.sparkbit.security.challenge.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder(toBuilder = true)
@Getter
@RequiredArgsConstructor
public class Credentials {

    private final String userId;
    private final String password;
    private final Boolean enabled;
    private final Boolean deleted;
}

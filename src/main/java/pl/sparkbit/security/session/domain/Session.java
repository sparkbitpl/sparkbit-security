package pl.sparkbit.security.session.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
public class Session {

    private final String authToken;
    private final String userId;
    private final Instant creation;
}

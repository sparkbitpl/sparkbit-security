package pl.sparkbit.security.domain;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder(toBuilder = true)
@Data
public class Session {

    private final String authToken;
    private final String userId;
    private final Instant creation;
    private final Instant expiresAt;
}

package pl.sparkbit.security.domain;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder(toBuilder = true)
@Data
public class SecurityChallenge {

    private final String id;
    private final String userId;
    private final SecurityChallengeType type;
    private final Instant expirationTimestamp;
    private final String token;
}

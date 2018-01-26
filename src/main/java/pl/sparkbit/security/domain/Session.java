package pl.sparkbit.security.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@RequiredArgsConstructor
@Builder(toBuilder = true)
@Getter
public class Session {

    private final String authToken;
    private final String userId;
    private final Instant creation;
}

package pl.sparkbit.security.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@SuppressWarnings("unused")
class SessionIdDTO {

    @JsonProperty("sid")
    private final String sessionId;
}

package pl.sparkbit.security.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class SessionIdDTO {

    @JsonProperty("sid")
    @SuppressWarnings("unused")
    private final String sessionId;
}

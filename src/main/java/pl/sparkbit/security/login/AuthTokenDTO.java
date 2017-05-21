package pl.sparkbit.security.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class AuthTokenDTO {

    @JsonProperty("authToken")
    @SuppressWarnings("unused")
    private final String authToken;
}

package pl.sparkbit.security.session.mvc.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthTokenDTO {

    @JsonProperty("authToken")
    @SuppressWarnings("unused")
    private final String authToken;
}

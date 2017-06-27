package pl.sparkbit.security.login;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@SuppressWarnings("unused")
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class LoginDTO implements AuthenticationTokenHolder {

    @JsonProperty
    private Map<String, String> authnAttributes;
}

package pl.sparkbit.security.login;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Map;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("unused")
public abstract class LoginDTO implements AuthenticationTokenHolder {

    @JsonProperty("authnAttributes")
    private Map<String, String> authnAttributesMap;

    @JsonProperty
    private Map<String, Object> additionalData;
}

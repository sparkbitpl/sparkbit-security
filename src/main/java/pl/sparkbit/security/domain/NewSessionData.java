package pl.sparkbit.security.domain;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder(toBuilder = true)
@Data
public class NewSessionData {

    private final String authToken;
    private final String userId;
    private final Map<String, Object> additionalData;
}

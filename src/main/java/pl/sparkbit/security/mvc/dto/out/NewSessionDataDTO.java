package pl.sparkbit.security.mvc.dto.out;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.sparkbit.security.domain.NewSessionData;

import java.util.Map;

@Getter
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class NewSessionDataDTO {

    private final String authToken;
    private final String userId;
    private final Map<String, Object> additionalData;

    public static NewSessionDataDTO from(NewSessionData sessionData) {
        return new NewSessionDataDTO(sessionData.getAuthToken(), sessionData.getUserId(),
                sessionData.getAdditionalData());
    }
}

package pl.sparkbit.security.domain;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor
@Builder(toBuilder = true)
@Data
public class TwitterCredentials implements Serializable {

    private final String oauthToken;
    private final String oauthTokenSecret;

}

package pl.sparkbit.security.social.resolver;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GoogleSecrets {

    private final List<String> googleClientIds;

}

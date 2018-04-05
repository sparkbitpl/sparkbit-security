package pl.sparkbit.security.login;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;

@Slf4j
public class AuthnAttributes extends HashMap<String, String> {

    private static final String SEPARATOR = "__XAX__";

    AuthnAttributes(Map<String, String> attributesMap) {
        Assert.notNull(attributesMap, "attributeMap cannot be null");
        Assert.notEmpty(attributesMap, "attributeMap cannot be empty");
        for (Map.Entry<String, String> entry : attributesMap.entrySet()) {
            String orgKey = entry.getKey();
            String underscoredKey = LOWER_CAMEL.to(LOWER_UNDERSCORE, orgKey);
            put(underscoredKey, entry.getValue());
        }
    }

    AuthnAttributes(String s) {
        Assert.notNull(s, "Input string cannot be null");
        Assert.hasLength(s, "Input string cannot be empty");
        int separatorIndex = s.indexOf(SEPARATOR);
        Assert.isTrue(separatorIndex > 0, "Input string must contain at least one separator");

        String[] fragments = s.split(SEPARATOR);
        Assert.isTrue(fragments.length % 2 == 0, "Invalid principal data format. Number of fragments must be even.");

        for (int i = 0; i < fragments.length; i += 2) {
            put(fragments[i], fragments[i + 1]);
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        forEach((n, v) -> result.append(n).append(SEPARATOR).append(v).append(SEPARATOR));
        result.delete(result.lastIndexOf(SEPARATOR), result.length());
        return result.toString();
    }
}

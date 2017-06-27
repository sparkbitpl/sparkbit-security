package pl.sparkbit.security.login;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import pl.sparkbit.security.social.GoogleLoginDTO;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginDTODeserializer extends StdDeserializer<LoginDTO> {
    private static final String TYPE_FIELD = "type";
    private static final String DEFAULT_TYPE = "password";
    private static final String GOOGLE_TYPE = "google";
    private final Map<String, Class<? extends LoginDTO>> registry;

    LoginDTODeserializer() {
        super(LoginDTO.class);
        registry = new HashMap<>();
        registry.put(DEFAULT_TYPE, PasswordLoginDTO.class);
        registry.put(GOOGLE_TYPE, GoogleLoginDTO.class);
    }

    public void registerLoginType(String type, Class<? extends LoginDTO> animalClass) {
        registry.put(type, animalClass);
    }

    @Override
    public LoginDTO deserialize(JsonParser jp,
            DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        ObjectNode root = mapper.readTree(jp);
        Class<? extends LoginDTO> loginClass;
        String typeValue = DEFAULT_TYPE;
        JsonNode type = root.get(TYPE_FIELD);
        if (type != null) {
            typeValue = type.textValue();
        }
        loginClass = registry.get(typeValue);
        if (loginClass == null) {
            throw JsonMappingException.from(jp, "Unknown LoginDTO type");
        }
        return mapper.readValue(root.traverse(), loginClass);
    }

}

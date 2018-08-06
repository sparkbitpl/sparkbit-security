package pl.sparkbit.security.callbacks;

import java.util.Map;

public interface LoginResponseAdditionalDataCallback {

    Map<String, Object> getAdditionalData();
}

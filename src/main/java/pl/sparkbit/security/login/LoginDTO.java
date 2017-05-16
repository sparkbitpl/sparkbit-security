package pl.sparkbit.security.login;

import lombok.Getter;

import java.util.Map;

@Getter
@SuppressWarnings("unused")
class LoginDTO {

    private Map<String, String> authnAttributes;
    private String password;
}

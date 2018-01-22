package pl.sparkbit.security.password.policy;

public interface PasswordPolicy {

    boolean isValid(String password);
}

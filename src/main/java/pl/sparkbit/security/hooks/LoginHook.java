package pl.sparkbit.security.hooks;

public interface LoginHook {

    void doAfterSuccessfulLogin(String userId);
}

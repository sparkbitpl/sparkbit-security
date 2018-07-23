package pl.sparkbit.security.hooks;

public interface LogoutHook {

    void doAfterSuccessfulLogout(String userId);
}

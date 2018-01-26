package pl.sparkbit.security.login.social.resolver;

import pl.sparkbit.security.login.AuthnAttributes;

public interface TwitterResolver {

    TwitterSecrets resolve(AuthnAttributes authn);
}

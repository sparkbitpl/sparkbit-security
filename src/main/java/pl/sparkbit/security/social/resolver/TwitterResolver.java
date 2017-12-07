package pl.sparkbit.security.social.resolver;

import pl.sparkbit.security.login.AuthnAttributes;

public interface TwitterResolver {

    TwitterSecrets resolve(AuthnAttributes authn);
}

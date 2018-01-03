package pl.sparkbit.security.session.auth.social.resolver;

import pl.sparkbit.security.session.auth.AuthnAttributes;

public interface TwitterResolver {

    TwitterSecrets resolve(AuthnAttributes authn);
}

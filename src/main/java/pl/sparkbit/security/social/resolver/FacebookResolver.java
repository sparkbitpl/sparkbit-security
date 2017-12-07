package pl.sparkbit.security.social.resolver;

import pl.sparkbit.security.login.AuthnAttributes;

public interface FacebookResolver {

    FacebookSecrets resolve(AuthnAttributes authn);

}

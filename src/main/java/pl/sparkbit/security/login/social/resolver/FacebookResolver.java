package pl.sparkbit.security.login.social.resolver;

import pl.sparkbit.security.login.AuthnAttributes;

public interface FacebookResolver {

    FacebookSecrets resolve(AuthnAttributes authn);

}

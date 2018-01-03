package pl.sparkbit.security.session.auth.social.resolver;

import pl.sparkbit.security.session.auth.AuthnAttributes;

public interface FacebookResolver {

    FacebookSecrets resolve(AuthnAttributes authn);

}

package pl.sparkbit.security.login.social.resolver;

import pl.sparkbit.security.login.AuthnAttributes;

public interface GoogleResolver {

    GoogleSecrets resolve(AuthnAttributes authn);

}

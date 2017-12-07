package pl.sparkbit.security.social.resolver;

import pl.sparkbit.security.login.AuthnAttributes;

public interface GoogleResolver {

    GoogleSecrets resolve(AuthnAttributes authn);

}

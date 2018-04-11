package pl.sparkbit.security.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sparkbit.security.dao.UserDetailsDao;
import pl.sparkbit.security.domain.RestUserDetails;
import pl.sparkbit.security.exception.SessionNotFoundException;
import pl.sparkbit.security.login.LoginPrincipal;
import pl.sparkbit.security.login.LoginPrincipalFactory;
import pl.sparkbit.security.login.LoginUserDetails;
import pl.sparkbit.security.password.encoder.AuthTokenHasher;
import pl.sparkbit.security.service.UserDetailsService;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
@SuppressWarnings("unused")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserDetailsDao userDetailsDao;
    private final AuthTokenHasher authTokenHasher;
    private final LoginPrincipalFactory loginPrincipalFactory;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String principalData) throws UsernameNotFoundException {
        LoginPrincipal principal = loginPrincipalFactory.generate(principalData);

        String userId = userDetailsDao.selectUserId(principal.getAuthnAttributes().withUnderscoredKeys())
                .orElseThrow((() -> new UsernameNotFoundException(principal + " not found")));

        Optional<LoginUserDetails> userDetails = userDetailsDao.selectLoginUserDetails(userId);
        return userDetails.orElseThrow(() -> new UsernameNotFoundException(principal + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public RestUserDetails retrieveRestUserDetails(String authToken) {
        String authTokenHash = authTokenHasher.hash(authToken);
        Optional<RestUserDetails> restUserDetails = userDetailsDao.selectRestUserDetails(authTokenHash);
        return restUserDetails.orElseThrow(() -> new SessionNotFoundException("Session not found"));
    }
}

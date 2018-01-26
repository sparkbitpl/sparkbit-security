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
import pl.sparkbit.security.login.LoginUserDetails;
import pl.sparkbit.security.service.UserDetailsService;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
@SuppressWarnings("unused")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserDetailsDao userDetailsDao;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String principalData) throws UsernameNotFoundException {
        LoginPrincipal principal = new LoginPrincipal(principalData);

        String userId = userDetailsDao.selectUserId(principal.getAuthnAttributes())
                .orElseThrow((() -> new UsernameNotFoundException(principal + " not found")));

        Optional<LoginUserDetails> userDetails = userDetailsDao.selectLoginUserDetails(userId);
        return userDetails.orElseThrow(() -> new UsernameNotFoundException(principal + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public RestUserDetails retrieveRestUserDetails(String authToken) {
        Optional<RestUserDetails> restUserDetails = userDetailsDao.selectRestUserDetails(authToken);
        return restUserDetails.orElseThrow(() -> new SessionNotFoundException("Session not found"));
    }
}

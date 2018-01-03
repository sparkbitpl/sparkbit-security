package pl.sparkbit.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.sparkbit.security.dao.SecurityDao;
import pl.sparkbit.security.rest.user.RestUserDetails;
import pl.sparkbit.security.rest.user.SessionNotFoundException;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class SecurityServiceImpl implements SecurityService {

    private final SecurityDao securityDao;

    @Override
    public RestUserDetails retrieveRestUserDetails(String authToken) {
        Optional<RestUserDetails> restUserDetails = securityDao.selectRestUserDetails(authToken);
        return restUserDetails.orElseThrow(() -> new SessionNotFoundException("Session not found"));
    }
}

package pl.sparkbit.security.rest.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.sparkbit.security.rest.dao.RestSecurityDao;
import pl.sparkbit.security.rest.domain.RestUserDetails;
import pl.sparkbit.security.rest.exception.SessionNotFoundException;
import pl.sparkbit.security.rest.service.RestSecurityService;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
@SuppressWarnings("unused")
public class RestSecurityServiceImpl implements RestSecurityService {

    private final RestSecurityDao restSecurityDao;

    @Override
    public RestUserDetails retrieveRestUserDetails(String authToken) {
        Optional<RestUserDetails> restUserDetails = restSecurityDao.selectRestUserDetails(authToken);
        return restUserDetails.orElseThrow(() -> new SessionNotFoundException("Session not found"));
    }
}

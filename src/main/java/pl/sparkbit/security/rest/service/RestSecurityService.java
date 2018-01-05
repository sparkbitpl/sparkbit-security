package pl.sparkbit.security.rest.service;

import pl.sparkbit.security.rest.domain.RestUserDetails;
import pl.sparkbit.security.rest.mvc.dto.in.ChangePasswordDTO;

public interface RestSecurityService {

    RestUserDetails retrieveRestUserDetails(String authToken);

    void changeCurrentUserPassword(ChangePasswordDTO dto);
}

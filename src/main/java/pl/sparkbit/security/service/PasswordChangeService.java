package pl.sparkbit.security.service;

import pl.sparkbit.security.mvc.dto.in.ChangePasswordDTO;

public interface PasswordChangeService {

    void changeCurrentUserPassword(ChangePasswordDTO dto);
}

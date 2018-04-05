package pl.sparkbit.security.service;

import pl.sparkbit.security.mvc.dto.in.ExtraAuthnCheckDTO;

public interface ExtraAuthnCheckService {

    void initiateExtraAuthnCheck(String userId);

    void performExtraAuthnCheck(ExtraAuthnCheckDTO dto);
}

package pl.sparkbit.security.dao.mybatis.data;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder(toBuilder = true)
@Getter
@RequiredArgsConstructor
public class SampleUser {
    private final String id;
    private final String username;
    private final String password;
}

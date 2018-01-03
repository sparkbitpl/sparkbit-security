package pl.sparkbit.security.dao.mybatis;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.sparkbit.security.rest.user.RestUserDetails;

import java.time.Instant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static pl.sparkbit.security.dao.mybatis.data.SecurityDbTables.PREFIX;
import static pl.sparkbit.security.dao.mybatis.data.SecurityTestData.CREDS_1;
import static pl.sparkbit.security.dao.mybatis.data.SecurityTestData.CREDS_1_USER_ID;
import static pl.sparkbit.security.dao.mybatis.data.SecurityTestData.ROLE_ADMIN;
import static pl.sparkbit.security.dao.mybatis.data.SecurityTestData.ROLE_USER;
import static pl.sparkbit.security.dao.mybatis.data.SecurityTestDataUtils.session;

@SuppressWarnings("SpringJavaAutowiringInspection")
public class SecurityMapperTest extends MapperTestBase {

    @Autowired
    private SecurityMapper securityMapper;

    @Test
    public void shouldSelectRestUserDetails() {
        String authToken = "id12345";
        Instant creation = Instant.ofEpochSecond(31232133);

        insertTestData(CREDS_1, session(authToken, CREDS_1_USER_ID, creation));

        RestUserDetails restUserDetails = securityMapper.selectRestUserDetails(authToken, PREFIX);
        assertNotNull(restUserDetails);
        assertEquals(authToken, restUserDetails.getAuthToken());
        assertEquals(CREDS_1_USER_ID, restUserDetails.getUserId());
        assertTrue(restUserDetails.getRoles() == restUserDetails.getAuthorities());
        assertEquals(2, restUserDetails.getRoles().size());
        assertTrue(restUserDetails.getRoles().contains(new SimpleGrantedAuthority(ROLE_ADMIN)));
        assertTrue(restUserDetails.getRoles().contains(new SimpleGrantedAuthority(ROLE_USER)));
    }
}

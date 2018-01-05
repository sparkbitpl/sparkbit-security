package pl.sparkbit.security.rest.dao.mybatis;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.sparkbit.security.rest.domain.RestUserDetails;

import java.time.Instant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static pl.sparkbit.security.rest.dao.mybatis.data.SecurityDbTables.CREDENTIALS;
import static pl.sparkbit.security.rest.dao.mybatis.data.SecurityDbTables.PREFIX;
import static pl.sparkbit.security.rest.dao.mybatis.data.SecurityTestData.*;
import static pl.sparkbit.security.rest.dao.mybatis.data.SecurityTestDataUtils.session;

@SuppressWarnings("SpringJavaAutowiringInspection")
public class RestSecurityMapperTest extends MapperTestBase {

    @Autowired
    private RestSecurityMapper restSecurityMapper;

    @Test
    public void shouldSelectRestUserDetails() {
        String authToken = "id12345";
        Instant creation = Instant.ofEpochSecond(31232133);

        insertTestData(CREDS_1, session(authToken, CREDS_1_USER_ID, creation));

        RestUserDetails restUserDetails = restSecurityMapper.selectRestUserDetails(authToken, PREFIX);
        assertNotNull(restUserDetails);
        assertEquals(authToken, restUserDetails.getAuthToken());
        assertEquals(CREDS_1_USER_ID, restUserDetails.getUserId());
        assertTrue(restUserDetails.getRoles() == restUserDetails.getAuthorities());
        assertEquals(2, restUserDetails.getRoles().size());
        assertTrue(restUserDetails.getRoles().contains(new SimpleGrantedAuthority(ROLE_ADMIN)));
        assertTrue(restUserDetails.getRoles().contains(new SimpleGrantedAuthority(ROLE_USER)));
    }

    @Test
    public void shouldSelectPassword() {
        insertTestData(CREDS_1);

        String password = restSecurityMapper.selectPasswordHashForUser(CREDS_1_USER_ID, PREFIX);
        assertEquals(CREDS_1_PASSWORD, password);
    }

    @Test
    public void shouldUpdatePassword() {
        insertTestData(CREDS_1);

        String newPassword = "xdasdasd";

        restSecurityMapper.updatePassword(CREDS_1_USER_ID, newPassword, PREFIX);

        assertEquals(1, countRowsInTableWhereColumnsEquals(CREDENTIALS,
                "user_id", quote(CREDS_1_USER_ID),
                "password", quote(newPassword)));
    }
}

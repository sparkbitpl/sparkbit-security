package pl.sparkbit.security.dao.mybatis;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.sparkbit.security.dao.mybatis.data.SecurityDbTables;
import pl.sparkbit.security.dao.mybatis.data.SecurityTestData;
import pl.sparkbit.security.dao.mybatis.data.SecurityTestDataUtils;
import pl.sparkbit.security.domain.Credentials;
import pl.sparkbit.security.domain.RestUserDetails;

import java.time.Instant;

import static org.junit.Assert.*;
import static pl.sparkbit.security.dao.mybatis.data.SecurityTestDataUtils.session;

@SuppressWarnings("SpringJavaAutowiringInspection")
public class RestSecurityMapperTest extends MapperTestBase {

    @Autowired
    private RestSecurityMapper restSecurityMapper;

    @SuppressWarnings("ConstantConditions")
    @Test
    public void shouldInsertCredentials() {
        String userId = "userid";
        String password = "passwd";
        Boolean enabled = false;
        Boolean deleted = true;
        Credentials credentials =
                Credentials.builder().userId(userId).password(password).enabled(enabled).deleted(deleted).build();

        restSecurityMapper.insertCredentials(credentials, SecurityDbTables.PREFIX);

        assertEquals(1, countRowsInTableWhereColumnsEquals(SecurityDbTables.CREDENTIALS,
                "user_id", quote(userId),
                "password", quote(password),
                "enabled", enabled,
                "deleted", deleted));
    }

    @Test
    public void shouldInsertRoleForUser() {
        insertTestData(SecurityTestData.CREDS_1);

        GrantedAuthority role = new SimpleGrantedAuthority("ROLE_XXX");

        restSecurityMapper.insertUserRole(SecurityTestData.CREDS_1_USER_ID, role);

        assertEquals(1, countRowsInTableWhereColumnsEquals(SecurityDbTables.USER_ROLE,
                "user_id", quote(SecurityTestData.CREDS_1_USER_ID),
                "role", quote(role)));
    }

    @Test
    public void shouldSelectRestUserDetails() {
        String authToken = "id12345";
        Instant creation = Instant.ofEpochSecond(31232133);

        insertTestData(SecurityTestData.CREDS_1, SecurityTestDataUtils.session(authToken, SecurityTestData.CREDS_1_USER_ID, creation));

        RestUserDetails restUserDetails = restSecurityMapper.selectRestUserDetails(authToken, SecurityDbTables.PREFIX);
        assertNotNull(restUserDetails);
        assertEquals(authToken, restUserDetails.getAuthToken());
        Assert.assertEquals(SecurityTestData.CREDS_1_USER_ID, restUserDetails.getUserId());
        assertTrue(restUserDetails.getRoles() == restUserDetails.getAuthorities());
        assertEquals(2, restUserDetails.getRoles().size());
        assertTrue(restUserDetails.getRoles().contains(new SimpleGrantedAuthority(SecurityTestData.ROLE_ADMIN)));
        assertTrue(restUserDetails.getRoles().contains(new SimpleGrantedAuthority(SecurityTestData.ROLE_USER)));
    }

    @Test
    public void shouldSelectPassword() {
        insertTestData(SecurityTestData.CREDS_1);

        String password = restSecurityMapper.selectPasswordHashForUser(SecurityTestData.CREDS_1_USER_ID, SecurityDbTables.PREFIX);
        Assert.assertEquals(SecurityTestData.CREDS_1_PASSWORD, password);
    }

    @Test
    public void shouldUpdateCredentials() {
        insertTestData(SecurityTestData.CREDS_1);

        String newPassword = "xdasdasd";
        Credentials credentials = Credentials.builder().userId(SecurityTestData.CREDS_1_USER_ID).password(newPassword)
                .enabled(!SecurityTestData.CREDS_1_ENABLED).deleted(!SecurityTestData.CREDS_1_DELETED).build();

        restSecurityMapper.updateCredentials(credentials, SecurityDbTables.PREFIX);

        assertEquals(1, countRowsInTableWhereColumnsEquals(SecurityDbTables.CREDENTIALS,
                "user_id", quote(SecurityTestData.CREDS_1_USER_ID),
                "password", quote(newPassword),
                "enabled", !SecurityTestData.CREDS_1_ENABLED,
                "deleted", !SecurityTestData.CREDS_1_DELETED));
    }
}

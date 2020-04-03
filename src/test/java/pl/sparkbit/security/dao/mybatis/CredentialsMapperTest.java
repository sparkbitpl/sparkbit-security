package pl.sparkbit.security.dao.mybatis;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.sparkbit.security.dao.mybatis.data.SecurityDbTables;
import pl.sparkbit.security.dao.mybatis.data.SecurityTestData;
import pl.sparkbit.security.domain.Credentials;

import static org.junit.Assert.assertEquals;

public class CredentialsMapperTest extends MapperTest {

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private CredentialsMapper credentialsMapper;

    @SuppressWarnings("ConstantConditions")
    @Test
    public void shouldInsertCredentials() {
        String userId = "userid";
        String password = "passwd";
        Boolean enabled = false;
        Boolean deleted = true;
        Credentials credentials =
                Credentials.builder().userId(userId).password(password).enabled(enabled).deleted(deleted).build();

        credentialsMapper.insertCredentials(credentials, SecurityDbTables.PREFIX);

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

        credentialsMapper.insertUserRole(SecurityTestData.CREDS_1_USER_ID, role, SecurityDbTables.PREFIX);

        assertEquals(1, countRowsInTableWhereColumnsEquals(SecurityDbTables.USER_ROLE,
                "user_id", quote(SecurityTestData.CREDS_1_USER_ID),
                "role", quote(role)));
    }

    @Test
    public void shouldSelectPassword() {
        insertTestData(SecurityTestData.CREDS_1);

        String password = credentialsMapper.selectPasswordHashForUser(SecurityTestData.CREDS_1_USER_ID, SecurityDbTables.PREFIX);
        Assert.assertEquals(SecurityTestData.CREDS_1_PASSWORD, password);
    }

    @Test
    public void shouldUpdateCredentials() {
        insertTestData(SecurityTestData.CREDS_1);

        String newPassword = "xdasdasd";
        Credentials credentials = Credentials.builder().userId(SecurityTestData.CREDS_1_USER_ID).password(newPassword)
                .enabled(!SecurityTestData.CREDS_1_ENABLED).deleted(!SecurityTestData.CREDS_1_DELETED).build();

        credentialsMapper.updateCredentials(credentials, SecurityDbTables.PREFIX);

        assertEquals(1, countRowsInTableWhereColumnsEquals(SecurityDbTables.CREDENTIALS,
                "user_id", quote(SecurityTestData.CREDS_1_USER_ID),
                "password", quote(newPassword),
                "enabled", !SecurityTestData.CREDS_1_ENABLED,
                "deleted", !SecurityTestData.CREDS_1_DELETED));
    }
}

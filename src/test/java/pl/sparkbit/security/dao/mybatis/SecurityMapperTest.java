package pl.sparkbit.security.dao.mybatis;

import com.ninja_squad.dbsetup.operation.Operation;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.sparkbit.security.domain.Session;
import pl.sparkbit.security.login.AuthnAttributes;
import pl.sparkbit.security.login.LoginUserDetails;
import pl.sparkbit.security.rest.user.RestUserDetails;

import java.time.Instant;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static pl.sparkbit.security.dao.mybatis.data.SecurityDbTables.PREFIX;
import static pl.sparkbit.security.dao.mybatis.data.SecurityDbTables.SESSION;
import static pl.sparkbit.security.dao.mybatis.data.SecurityTestData.*;
import static pl.sparkbit.security.dao.mybatis.data.SecurityTestDataUtils.session;

@SuppressWarnings("SpringJavaAutowiringInspection")
public class SecurityMapperTest extends MapperTestBase {

    @Autowired
    private SecurityMapper securityMapper;

    @Test
    public void shouldInsertSession() {
        insertTestData(CREDS_1);

        String authToken = "id54321";
        Instant creation = Instant.ofEpochSecond(31232133);
        Session session = Session.builder()
                .authToken(authToken)
                .userId(CREDS_1_USER_ID)
                .creation(creation)
                .build();
        securityMapper.insertSession(session, PREFIX);

        assertEquals(1, countRowsInTableWhereColumnsEquals(SESSION,
                "auth_token", quote(authToken),
                "user_id", quote(CREDS_1_USER_ID),
                "creation_ts", creation.toEpochMilli(),
                "deleted_ts", null
        ));
    }

    @Test
    public void shouldSelectLoginUserDetails() {
        insertTestData(CREDS_1);

        AuthnAttributes authnAttributes =
                new AuthnAttributes(singletonMap("username", CREDS_1_USERNAME), singleton("username"));
        LoginUserDetails loginUserDetails = securityMapper.selectLoginUserDetails(authnAttributes, PREFIX);

        assertNotNull(loginUserDetails);
        assertEquals(CREDS_1_USER_ID, loginUserDetails.getUserId());
        assertEquals(CREDS_1_USER_ID, loginUserDetails.getUsername());
        assertEquals(CREDS_1_PASSWORD, loginUserDetails.getPassword());
        assertEquals(CREDS_1_ENABLED, loginUserDetails.isEnabled());
        assertEquals(CREDS_1_DELETED, loginUserDetails.getDeleted());
        assertNull(loginUserDetails.getAuthorities());
    }

    @Test
    public void shouldReturnNullWhenUserNotFound() {
        AuthnAttributes authnAttributes =
                new AuthnAttributes(singletonMap("username", CREDS_1_USERNAME), singleton("username"));
        LoginUserDetails loginUserDetails = securityMapper.selectLoginUserDetails(authnAttributes, PREFIX);

        assertNull(loginUserDetails);
    }

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

    @Test
    public void shouldSelectSession() {
        String authToken = "id12345";
        Instant creation = Instant.ofEpochSecond(31232133);

        insertTestData(CREDS_1, session(authToken, CREDS_1_USER_ID, creation));

        Session session = securityMapper.selectSession(authToken, PREFIX);
        assertNotNull(session);
        assertEquals(authToken, session.getAuthToken());
        assertEquals(CREDS_1_USER_ID, session.getUserId());
        assertEquals(creation, session.getCreation());
    }

    @Test
    public void shouldNotSelectSessionWhenIsDeleted() {
        String authToken = "id12345";
        Instant creation = Instant.ofEpochSecond(31232133);
        Instant deleted = Instant.ofEpochSecond(31232555);

        insertTestData(CREDS_1, session(authToken, CREDS_1_USER_ID, creation, deleted));

        Session session = securityMapper.selectSession(authToken, PREFIX);
        assertNull(session);
    }

    @Test
    public void shouldDeleteSession() {
        String authToken = "id12345";
        Instant creation = Instant.ofEpochSecond(31232133);
        Instant deletedTs = Instant.ofEpochSecond(31232555);

        insertTestData(CREDS_1, session(authToken, CREDS_1_USER_ID, creation));

        securityMapper.deleteSession(authToken, deletedTs, PREFIX);
        assertEquals(1, countRowsInTableWhereColumnsEquals(SESSION,
                "auth_token", quote(authToken),
                "deleted_ts", deletedTs.toEpochMilli()
        ));
    }

    @Test
    public void shouldDeleteOldSessions() {
        String authToken = "id12345";
        String authToken2 = "id123456";
        Instant creation = Instant.ofEpochSecond(31232133);
        Instant deletedTs = Instant.ofEpochSecond(31232555);
        Instant olderThan = deletedTs.plusSeconds(3600);

        Operation session1 = session(authToken, CREDS_1_USER_ID, creation, deletedTs);
        Operation session2 = session(authToken2, CREDS_1_USER_ID, creation, deletedTs);
        insertTestData(CREDS_1, session1, session2);

        securityMapper.deleteSessions(olderThan, PREFIX);

        assertEquals(0, countRowsInTableWhereColumnsEquals(SESSION,
                "user_id", quote(CREDS_1_USER_ID)
        ));
    }

}

package pl.sparkbit.security.dao.mybatis;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.ninja_squad.dbsetup.operation.Operation;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.sparkbit.security.login.AuthnAttributes;
import pl.sparkbit.security.login.LoginUserDetails;
import pl.sparkbit.security.domain.Session;

import java.time.Instant;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.*;
import static pl.sparkbit.security.dao.mybatis.data.SecurityDbTables.*;
import static pl.sparkbit.security.dao.mybatis.data.SecurityTestData.*;
import static pl.sparkbit.security.dao.mybatis.data.SecurityTestDataUtils.session;

@SuppressWarnings("SpringJavaAutowiringInspection")
public class SessionMapperTest extends MapperTestBase {

    @Autowired
    private SessionMapper sessionMapper;

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
        sessionMapper.insertSession(session, PREFIX);

        assertEquals(1, countRowsInTableWhereColumnsEquals(SESSION,
                "auth_token", quote(authToken),
                "user_id", quote(CREDS_1_USER_ID),
                "creation_ts", creation.toEpochMilli(),
                "deleted_ts", null
        ));
    }

    @Test
    public void shouldSelectSession() {
        String authToken = "id12345";
        Instant creation = Instant.ofEpochSecond(31232133);

        insertTestData(CREDS_1, session(authToken, CREDS_1_USER_ID, creation));

        Session session = sessionMapper.selectSession(authToken, PREFIX);
        assertNotNull(session);
        assertEquals(authToken, session.getAuthToken());
        assertEquals(CREDS_1_USER_ID, session.getUserId());
        assertEquals(creation, session.getCreation());
    }

    @Test
    public void shouldSelectUserIdForSimpleLogin() {
        insertTestData(CREDS_1, SIMPLE_LOGIN_USER_1);

        AuthnAttributes authnAttributes =
                new AuthnAttributes(singletonMap("username", USER_1_USERNAME), singleton("username"));
        String userId = sessionMapper.selectUserId(SIMPLE_LOGIN_USER, authnAttributes, PREFIX);
        assertEquals(USER_1_ID, userId);
    }

    @Test
    public void shouldSelectUserIdForCompositeLogin() {
        insertTestData(CREDS_1, COMPOSITE_LOGIN_USER_1);

        AuthnAttributes authnAttributes = new AuthnAttributes(
                ImmutableMap.of("username", USER_1_USERNAME, "context", USER_1_CONTEXT),
                ImmutableSet.of("username", "context"));
        String userId = sessionMapper.selectUserId(COMPOSITE_LOGIN_USER, authnAttributes, PREFIX);
        assertEquals(USER_1_ID, userId);
    }

    @Test
    public void shouldNotSelectSessionWhenIsDeleted() {
        String authToken = "id12345";
        Instant creation = Instant.ofEpochSecond(31232133);
        Instant deleted = Instant.ofEpochSecond(31232555);

        insertTestData(CREDS_1, session(authToken, CREDS_1_USER_ID, creation, deleted));

        Session session = sessionMapper.selectSession(authToken, PREFIX);
        assertNull(session);
    }

    @Test
    public void shouldSelectLoginUserDetails() {
        insertTestData(CREDS_1);
        LoginUserDetails loginUserDetails = sessionMapper.selectLoginUserDetails(CREDS_1_USER_ID, PREFIX);

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
        LoginUserDetails loginUserDetails = sessionMapper.selectLoginUserDetails(CREDS_1_USER_ID, PREFIX);
        assertNull(loginUserDetails);
    }

    @Test
    public void shouldDeleteSession() {
        String authToken = "id12345";
        Instant creation = Instant.ofEpochSecond(31232133);
        Instant deletedTs = Instant.ofEpochSecond(31232555);

        insertTestData(CREDS_1, session(authToken, CREDS_1_USER_ID, creation));

        sessionMapper.deleteSession(authToken, deletedTs, PREFIX);
        assertEquals(1, countRowsInTableWhereColumnsEquals(SESSION,
                "auth_token", quote(authToken),
                "deleted_ts", deletedTs.toEpochMilli()
        ));
    }

    @Test
    public void shouldDeleteExpiredSessions() {
        String authToken = "id12345";
        String authToken2 = "id123456";
        Instant creation = Instant.ofEpochSecond(31232133);
        Instant deletedTs1 = Instant.ofEpochSecond(31232555);
        Instant deletedTs2 = deletedTs1.plusSeconds(5000);
        Instant olderThan = deletedTs1.plusSeconds(3600);

        Operation session1 = session(authToken, CREDS_1_USER_ID, creation, deletedTs1);
        Operation session2 = session(authToken2, CREDS_1_USER_ID, creation, deletedTs2);
        insertTestData(CREDS_1, session1, session2);

        sessionMapper.deleteExpiredSessions(olderThan, PREFIX);

        assertEquals(1, countRowsInTableWhereColumnsEquals(SESSION, "user_id", quote(CREDS_1_USER_ID)));
    }

    @Test
    public void shouldDeleteSessionsForUser() {
        String authToken = "id12345";
        String authToken2 = "id123456";
        Instant creation = Instant.ofEpochSecond(31232133);
        Instant deletedTs = Instant.ofEpochSecond(31232555);

        Operation session1 = session(authToken, CREDS_1_USER_ID, creation);
        Operation session2 = session(authToken2, CREDS_1_USER_ID, creation);
        insertTestData(CREDS_1, session1, session2);

        sessionMapper.deleteSessionsForUser(CREDS_1_USER_ID, deletedTs, PREFIX);

        assertEquals(2, countRowsInTableWhereColumnsEquals(SESSION,
                "user_id", quote(CREDS_1_USER_ID),
                "deleted_ts", deletedTs.toEpochMilli())
        );
    }

}

package pl.sparkbit.security.dao.mybatis;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.sparkbit.security.domain.Session;
import pl.sparkbit.security.login.AuthnAttributes;
import pl.sparkbit.security.login.LoginUserDetails;
import pl.sparkbit.security.rest.RestUserDetails;

import java.time.Instant;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static pl.sparkbit.security.dao.mybatis.data.SecurityDbTables.SESSION;
import static pl.sparkbit.security.dao.mybatis.data.SecurityTestData.ROLE_ADMIN;
import static pl.sparkbit.security.dao.mybatis.data.SecurityTestData.ROLE_USER;
import static pl.sparkbit.security.dao.mybatis.data.SecurityTestData.USER_1;
import static pl.sparkbit.security.dao.mybatis.data.SecurityTestData.USER_1_ID;
import static pl.sparkbit.security.dao.mybatis.data.SecurityTestData.USER_1_PASSWORD;
import static pl.sparkbit.security.dao.mybatis.data.SecurityTestData.USER_1_USERNAME;
import static pl.sparkbit.security.dao.mybatis.data.SecurityTestDataUtils.session;

@SuppressWarnings("SpringJavaAutowiringInspection")
public class SecurityMapperTest extends MapperTestBase {

    @Autowired
    private SecurityMapper securityMapper;

    @Test
    public void shouldInsertSession() {
        insertTestData(USER_1);

        String sessionId = "id54321";
        Instant creation = Instant.ofEpochSecond(31232133);
        Session session = Session.builder()
                .id(sessionId)
                .userId(USER_1_ID)
                .creation(creation)
                .build();
        securityMapper.insertSession(session);

        assertEquals(1, countRowsInTableWhereColumnsEquals(SESSION,
                "id", quote(sessionId),
                "user_id", quote(USER_1_ID),
                "creation_ts", creation.toEpochMilli()));
    }

    @Test
    public void shouldSelectLoginUserDetails() {
        insertTestData(USER_1);

        AuthnAttributes authnAttributes =
                new AuthnAttributes(singletonMap("username", USER_1_USERNAME), singleton("username"));
        LoginUserDetails loginUserDetails = securityMapper.selectLoginUserDetails(authnAttributes);

        assertNotNull(loginUserDetails);
        assertEquals(USER_1_ID, loginUserDetails.getUserId());
        assertEquals(USER_1_ID, loginUserDetails.getUsername());
        assertEquals(USER_1_PASSWORD, loginUserDetails.getPassword());
        assertNull(loginUserDetails.getAuthorities());
    }

    @Test
    public void shouldReturnNullWhenUserNotFound() {
        AuthnAttributes authnAttributes =
                new AuthnAttributes(singletonMap("username", USER_1_USERNAME), singleton("username"));
        LoginUserDetails loginUserDetails = securityMapper.selectLoginUserDetails(authnAttributes);

        assertNull(loginUserDetails);
    }

    @Test
    public void shouldSelectRestUserDetails() {
        String sessionId = "id12345";
        Instant creation = Instant.ofEpochSecond(31232133);

        insertTestData(USER_1, session(sessionId, USER_1_ID, creation));

        RestUserDetails restUserDetails = securityMapper.selectRestUserDetails(sessionId);
        assertNotNull(restUserDetails);
        assertEquals(sessionId, restUserDetails.getSessionId());
        assertEquals(USER_1_ID, restUserDetails.getUserId());
        assertTrue(restUserDetails.getRoles() == restUserDetails.getAuthorities());
        assertEquals(2, restUserDetails.getRoles().size());
        assertTrue(restUserDetails.getRoles().contains(new SimpleGrantedAuthority(ROLE_ADMIN)));
        assertTrue(restUserDetails.getRoles().contains(new SimpleGrantedAuthority(ROLE_USER)));
    }

    @Test
    public void shouldSelectSession() {
        String id = "id12345";
        Instant creation = Instant.ofEpochSecond(31232133);

        insertTestData(USER_1, session(id, USER_1_ID, creation));

        Session session = securityMapper.selectSession(id);
        assertNotNull(session);
        assertEquals(id, session.getId());
        assertEquals(USER_1_ID, session.getUserId());
        assertEquals(creation, session.getCreation());
    }

    @Test
    public void shouldDeleteSession() {
        String sessionId = "id12345";
        Instant creation = Instant.ofEpochSecond(31232133);

        insertTestData(USER_1, session(sessionId, USER_1_ID, creation));

        securityMapper.deleteSession(sessionId);
        assertEquals(0, countRowsInTableWhereColumnsEquals(SESSION, "id", quote(sessionId)));
    }
}

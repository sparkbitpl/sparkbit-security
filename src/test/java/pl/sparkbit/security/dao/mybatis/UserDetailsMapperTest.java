package pl.sparkbit.security.dao.mybatis;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.sparkbit.security.dao.mybatis.data.SecurityDbTables;
import pl.sparkbit.security.dao.mybatis.data.SecurityTestData;
import pl.sparkbit.security.domain.RestUserDetails;
import pl.sparkbit.security.login.AuthnAttributes;
import pl.sparkbit.security.login.LoginUserDetails;

import java.time.Instant;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.*;
import static pl.sparkbit.security.dao.mybatis.data.SecurityDbTables.*;
import static pl.sparkbit.security.dao.mybatis.data.SecurityTestData.*;
import static pl.sparkbit.security.dao.mybatis.data.SecurityTestDataUtils.session;

public class UserDetailsMapperTest extends MapperTestBase {

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private UserDetailsMapper userDetailsMapper;

    @Test
    public void shouldSelectLoginUserDetails() {
        insertTestData(CREDS_1);
        LoginUserDetails loginUserDetails = userDetailsMapper.selectLoginUserDetails(CREDS_1_USER_ID, PREFIX);

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
        LoginUserDetails loginUserDetails = userDetailsMapper.selectLoginUserDetails(CREDS_1_USER_ID, PREFIX);
        assertNull(loginUserDetails);
    }

    @Test
    public void shouldSelectRestUserDetails() {
        String authToken = "id12345";
        Instant creation = Instant.ofEpochSecond(31232133);

        insertTestData(SecurityTestData.CREDS_1, session(authToken, CREDS_1_USER_ID, creation));

        RestUserDetails restUserDetails = userDetailsMapper.selectRestUserDetails(authToken, SecurityDbTables.PREFIX);
        assertNotNull(restUserDetails);
        assertEquals(authToken, restUserDetails.getAuthToken());
        Assert.assertEquals(CREDS_1_USER_ID, restUserDetails.getUserId());
        assertTrue(restUserDetails.getRoles() == restUserDetails.getAuthorities());
        assertEquals(2, restUserDetails.getRoles().size());
        assertTrue(restUserDetails.getRoles().contains(new SimpleGrantedAuthority(SecurityTestData.ROLE_ADMIN)));
        assertTrue(restUserDetails.getRoles().contains(new SimpleGrantedAuthority(SecurityTestData.ROLE_USER)));
    }

    @Test
    public void shouldSelectUserIdForSimpleLogin() {
        insertTestData(CREDS_1, SIMPLE_LOGIN_USER_1);

        AuthnAttributes authnAttributes =
                new AuthnAttributes(singletonMap("username", USER_1_USERNAME), singleton("username"));
        String userId = userDetailsMapper.selectUserId(SIMPLE_LOGIN_USER, USER_ID_COLUMN_NAME, authnAttributes, PREFIX);
        assertEquals(USER_1_ID, userId);
    }

    @Test
    public void shouldSelectUserIdForCompositeLogin() {
        insertTestData(CREDS_1, COMPOSITE_LOGIN_USER_1);

        AuthnAttributes authnAttributes = new AuthnAttributes(
                ImmutableMap.of("username", USER_1_USERNAME, "context", USER_1_CONTEXT),
                ImmutableSet.of("username", "context"));
        String userId = userDetailsMapper.selectUserId(COMPOSITE_LOGIN_USER, USER_ID_COLUMN_NAME, authnAttributes,
                PREFIX);
        assertEquals(USER_1_ID, userId);
    }
}

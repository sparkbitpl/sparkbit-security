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
import pl.sparkbit.security.login.LoginPrincipalFactory;
import pl.sparkbit.security.login.LoginUserDetails;

import java.time.Instant;
import java.util.Collections;

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
        String authTokenHash = "UNIMruUOkLkovQ2TuXGZqAtKt6V6yl2WRUOAQP2763V3z1CNa0QIgHqWdnIpHTpF";
        Instant creationTimestamp = Instant.ofEpochSecond(31232133);

        insertTestData(SecurityTestData.CREDS_1, session(authTokenHash, CREDS_1_USER_ID, creationTimestamp));

        RestUserDetails restUserDetails = userDetailsMapper.selectRestUserDetails(authTokenHash, SecurityDbTables.PREFIX);
        assertNotNull(restUserDetails);
        assertEquals(authTokenHash, restUserDetails.getAuthTokenHash());
        Assert.assertEquals(CREDS_1_USER_ID, restUserDetails.getUserId());
        assertTrue(restUserDetails.getRoles() == restUserDetails.getAuthorities());
        assertEquals(2, restUserDetails.getRoles().size());
        assertTrue(restUserDetails.getRoles().contains(new SimpleGrantedAuthority(SecurityTestData.ROLE_ADMIN)));
        assertTrue(restUserDetails.getRoles().contains(new SimpleGrantedAuthority(SecurityTestData.ROLE_USER)));
        assertFalse(restUserDetails.isExtraAuthnCheckRequired());
    }

    @Test
    public void shouldSelectRestUserDetailsWithAdditionalAuthCheckRequired() {
        String authTokenHash = "UNIMruUOkLkovQ2TuXGZqAtKt6V6yl2WRUOAQP2763V3z1CNa0QIgHqWdnIpHTpF";
        Instant timestamp = Instant.ofEpochSecond(31232133);

        insertTestData(SecurityTestData.CREDS_1, session(authTokenHash, CREDS_1_USER_ID, timestamp, timestamp, null,
                true));

        RestUserDetails restUserDetails = userDetailsMapper.selectRestUserDetails(authTokenHash, SecurityDbTables.PREFIX);
        assertNotNull(restUserDetails);
        assertEquals(authTokenHash, restUserDetails.getAuthTokenHash());
        Assert.assertEquals(CREDS_1_USER_ID, restUserDetails.getUserId());
        assertTrue(restUserDetails.getRoles() == restUserDetails.getAuthorities());
        assertEquals(2, restUserDetails.getRoles().size());
        assertTrue(restUserDetails.getRoles().contains(new SimpleGrantedAuthority(SecurityTestData.ROLE_ADMIN)));
        assertTrue(restUserDetails.getRoles().contains(new SimpleGrantedAuthority(SecurityTestData.ROLE_USER)));
        assertTrue(restUserDetails.isExtraAuthnCheckRequired());
    }

    @Test
    public void shouldSelectUserIdForSimpleLogin() {
        insertTestData(CREDS_1, SIMPLE_LOGIN_USER_1);

        AuthnAttributes authnAttributes = new LoginPrincipalFactory(Collections.singleton("username"))
                .generate(singletonMap("username", USER_1_USERNAME))
                .getAuthnAttributes();
        String userId = userDetailsMapper.selectUserId(SIMPLE_LOGIN_USER, USER_ID_COLUMN_NAME, authnAttributes, PREFIX);
        assertEquals(USER_1_ID, userId);
    }

    @Test
    public void shouldSelectUserIdForCompositeLogin() {
        insertTestData(CREDS_1, COMPOSITE_LOGIN_USER_1);

        AuthnAttributes authnAttributes = new LoginPrincipalFactory(ImmutableSet.of("username", "context"))
                .generate(ImmutableMap.of("username", USER_1_USERNAME, "context", USER_1_CONTEXT))
                .getAuthnAttributes();
        String userId = userDetailsMapper.selectUserId(COMPOSITE_LOGIN_USER, USER_ID_COLUMN_NAME, authnAttributes,
                PREFIX);
        assertEquals(USER_1_ID, userId);
    }
}

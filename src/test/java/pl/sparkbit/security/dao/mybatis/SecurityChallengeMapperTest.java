package pl.sparkbit.security.dao.mybatis;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.sparkbit.security.dao.mybatis.data.SecurityDbTables;
import pl.sparkbit.security.dao.mybatis.data.SecurityTestDataUtils;
import pl.sparkbit.security.domain.SecurityChallenge;
import pl.sparkbit.security.domain.SecurityChallengeType;

import java.time.Instant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static pl.sparkbit.security.domain.SecurityChallengeType.EMAIL_VERIFICATION;
import static pl.sparkbit.security.domain.SecurityChallengeType.PASSWORD_RESET;
import static pl.sparkbit.security.dao.mybatis.data.SecurityTestData.*;

public class SecurityChallengeMapperTest extends MapperTestBase {

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private SecurityChallengeMapper securityChallengeMapper;

    @Test
    public void shouldInsertChallenge() {
        insertTestData(CREDS_1);

        String id = "zxcz";
        SecurityChallengeType type = EMAIL_VERIFICATION;
        Instant expirationTimestamp = Instant.ofEpochSecond(31232133);
        String token = "id54321";

        SecurityChallenge securityChallenge = SecurityChallenge.builder()
                .id(id)
                .userId(CREDS_1_USER_ID)
                .type(type)
                .expirationTimestamp(expirationTimestamp)
                .token(token)
                .build();

        securityChallengeMapper.insertChallenge(securityChallenge, SecurityDbTables.PREFIX);

        assertEquals(1, countRowsInTableWhereColumnsEquals(SecurityDbTables.SECURITY_CHALLENGE,
                "id", quote(id),
                "user_id", quote(CREDS_1_USER_ID),
                "challenge_type", quote(type),
                "expiration_ts", expirationTimestamp.toEpochMilli(),
                "token", quote(token)
        ));
    }

    @Test
    public void shouldSelectSecurityChallenge() {
        insertTestData(CREDS_1, CHALLENGE_1);

        SecurityChallenge challenge =
                securityChallengeMapper.selectChallengeByTokenAndType(CHALLENGE_1_TOKEN, CHALLENGE_1_TYPE, SecurityDbTables.PREFIX);

        assertNotNull(challenge);
        assertEquals(CHALLENGE_1_ID, challenge.getId());
        assertEquals(CREDS_1_USER_ID, challenge.getUserId());
        assertEquals(CHALLENGE_1_TYPE, challenge.getType());
        assertEquals(CHALLENGE_1_EXPIRATION_TIMESTAMP, challenge.getExpirationTimestamp());
        assertEquals(CHALLENGE_1_TOKEN, challenge.getToken());
    }

    @Test
    public void shouldDeleteSecurityChallengeById() {
        insertTestData(CREDS_1, CHALLENGE_1);
        securityChallengeMapper.deleteChallengeById(CHALLENGE_1_ID, SecurityDbTables.PREFIX);
        assertEquals(0, countRowsInTable(SecurityDbTables.SECURITY_CHALLENGE));
    }

    @Test
    public void shouldDeleteSecurityChallengeByUserIdAndType() {
        insertTestData(CREDS_1, CHALLENGE_1);
        securityChallengeMapper.deleteChallengeByUserIdAndType(CREDS_1_USER_ID, CHALLENGE_1_TYPE, SecurityDbTables.PREFIX);
        assertEquals(0, countRowsInTable(SecurityDbTables.SECURITY_CHALLENGE));
    }

    @Test
    public void shouldDeleteExpiredSecurityChallenges() {
        Instant past = Instant.ofEpochSecond(10000);
        Instant now = Instant.ofEpochSecond(20000);
        Instant future = Instant.ofEpochSecond(30000);
        insertTestData(CREDS_1, SecurityTestDataUtils.securityChallenge("1", CREDS_1_USER_ID, PASSWORD_RESET, past, "1"),
                SecurityTestDataUtils.securityChallenge("2", CREDS_1_USER_ID, EMAIL_VERIFICATION, future, "2"));

        securityChallengeMapper.deleteExpiredChallenges(now, SecurityDbTables.PREFIX);
        assertEquals(1, countRowsInTable(SecurityDbTables.SECURITY_CHALLENGE));
    }
}

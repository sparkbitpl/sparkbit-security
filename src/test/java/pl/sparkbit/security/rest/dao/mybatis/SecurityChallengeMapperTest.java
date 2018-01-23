package pl.sparkbit.security.rest.dao.mybatis;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.sparkbit.security.challenge.dao.mybatis.SecurityChallengeMapper;
import pl.sparkbit.security.challenge.domain.SecurityChallenge;
import pl.sparkbit.security.challenge.domain.SecurityChallengeType;

import java.time.Instant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static pl.sparkbit.security.challenge.domain.SecurityChallengeType.EMAIL_VERIFICATION;
import static pl.sparkbit.security.challenge.domain.SecurityChallengeType.PASSWORD_RESET;
import static pl.sparkbit.security.rest.dao.mybatis.data.SecurityDbTables.PREFIX;
import static pl.sparkbit.security.rest.dao.mybatis.data.SecurityDbTables.SECURITY_CHALLENGE;
import static pl.sparkbit.security.rest.dao.mybatis.data.SecurityTestData.*;
import static pl.sparkbit.security.rest.dao.mybatis.data.SecurityTestDataUtils.securityChallenge;

@SuppressWarnings("SpringJavaAutowiringInspection")
public class SecurityChallengeMapperTest extends MapperTestBase {

    @Autowired
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

        securityChallengeMapper.insertChallenge(securityChallenge, PREFIX);

        assertEquals(1, countRowsInTableWhereColumnsEquals(SECURITY_CHALLENGE,
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
                securityChallengeMapper.selectChallengeByToken(CHALLENGE_1_TOKEN, CHALLENGE_1_TYPE, PREFIX);

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
        securityChallengeMapper.deleteChallengeById(CHALLENGE_1_ID);
        assertEquals(0, countRowsInTable(SECURITY_CHALLENGE));
    }

    @Test
    public void shouldDeleteSecurityChallengeByUserIdAndType() {
        insertTestData(CREDS_1, CHALLENGE_1);
        securityChallengeMapper.deleteChallengeByUserIdAndType(CREDS_1_USER_ID, CHALLENGE_1_TYPE, PREFIX);
        assertEquals(0, countRowsInTable(SECURITY_CHALLENGE));
    }

    @Test
    public void shouldDeleteExpiredSecurityChallenges() {
        Instant past = Instant.ofEpochSecond(10000);
        Instant now = Instant.ofEpochSecond(20000);
        Instant future = Instant.ofEpochSecond(30000);
        insertTestData(CREDS_1, securityChallenge("1", CREDS_1_USER_ID, PASSWORD_RESET, past, "1"),
                securityChallenge("2", CREDS_1_USER_ID, EMAIL_VERIFICATION, future, "2"));

        securityChallengeMapper.deleteExpiredChallenges(now);
        assertEquals(1, countRowsInTable(SECURITY_CHALLENGE));
    }
}

package pl.sparkbit.security.dao.mybatis;

import com.ninja_squad.dbsetup.operation.Operation;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.sparkbit.security.domain.Session;

import java.time.Instant;

import static org.junit.Assert.*;
import static pl.sparkbit.security.dao.mybatis.data.SecurityDbTables.PREFIX;
import static pl.sparkbit.security.dao.mybatis.data.SecurityDbTables.SESSION;
import static pl.sparkbit.security.dao.mybatis.data.SecurityTestData.CREDS_1;
import static pl.sparkbit.security.dao.mybatis.data.SecurityTestData.CREDS_1_USER_ID;
import static pl.sparkbit.security.dao.mybatis.data.SecurityTestDataUtils.session;

public class SessionMapperTest extends MapperTestBase {

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private SessionMapper sessionMapper;

    @Test
    public void shouldInsertSession() {
        insertTestData(CREDS_1);

        String authToken = "id54321";
        Instant creationTimestamp = Instant.ofEpochSecond(31232133);
        Instant expirationTimestamp = Instant.ofEpochSecond(756756756);
        Session session = Session.builder()
                .authToken(authToken)
                .userId(CREDS_1_USER_ID)
                .creationTimestamp(creationTimestamp)
                .expirationTimestamp(expirationTimestamp)
                .build();
        sessionMapper.insertSession(session, PREFIX);

        assertEquals(1, countRowsInTableWhereColumnsEquals(SESSION,
                "auth_token", quote(authToken),
                "user_id", quote(CREDS_1_USER_ID),
                "creation_ts", creationTimestamp.toEpochMilli(),
                "deletion_ts", null,
                "expiration_ts", expirationTimestamp.toEpochMilli()
        ));
    }

    @Test
    public void shouldUpdateSessionExpirationTimestamp() {
        String authToken = "id12345";
        Instant creationTimestamp = Instant.ofEpochSecond(31232133);
        Instant expirationTimestamp = Instant.ofEpochSecond(31232133);
        Instant deletionTimestamp = Instant.ofEpochSecond(31232133);
        Instant updatedExpirationTimestamp = Instant.ofEpochSecond(44444444);

        insertTestData(CREDS_1,
                session(authToken, CREDS_1_USER_ID, creationTimestamp, expirationTimestamp, deletionTimestamp));

        sessionMapper.updateSessionExpirationTimestamp(authToken, updatedExpirationTimestamp, PREFIX);
        assertEquals(1, countRowsInTableWhereColumnsEquals(SESSION,
                "auth_token", quote(authToken),
                "expiration_ts", updatedExpirationTimestamp.toEpochMilli()
        ));
    }

    @Test
    public void shouldSelectSession() {
        String authToken = "id12345";
        Instant creationTimestamp = Instant.ofEpochSecond(31232133);

        insertTestData(CREDS_1, session(authToken, CREDS_1_USER_ID, creationTimestamp));

        Session session = sessionMapper.selectSession(authToken, PREFIX);
        assertNotNull(session);
        assertEquals(authToken, session.getAuthToken());
        assertEquals(CREDS_1_USER_ID, session.getUserId());
        assertEquals(creationTimestamp, session.getCreationTimestamp());
    }

    @Test
    public void shouldNotSelectSessionWhenIsDeleted() {
        String authToken = "id12345";
        Instant creationTimestamp = Instant.ofEpochSecond(31232133);
        Instant deletionTimestamp = Instant.ofEpochSecond(31232555);

        insertTestData(CREDS_1, session(authToken, CREDS_1_USER_ID, creationTimestamp, null, deletionTimestamp));

        Session session = sessionMapper.selectSession(authToken, PREFIX);
        assertNull(session);
    }

    @Test
    public void shouldDeleteSession() {
        String authToken = "id12345";
        Instant creationTimestamp = Instant.ofEpochSecond(31232133);
        Instant deletionTimestamp = Instant.ofEpochSecond(31232555);

        insertTestData(CREDS_1, session(authToken, CREDS_1_USER_ID, creationTimestamp));

        sessionMapper.deleteSession(authToken, deletionTimestamp, PREFIX);
        assertEquals(1, countRowsInTableWhereColumnsEquals(SESSION,
                "auth_token", quote(authToken),
                "deletion_ts", deletionTimestamp.toEpochMilli()
        ));
    }

    @Test
    public void shouldDeleteSessionsForUser() {
        String authToken = "id12345";
        String authToken2 = "id123456";
        Instant creationTimestamp = Instant.ofEpochSecond(31232133);
        Instant deletionTimestamp = Instant.ofEpochSecond(31232555);

        Operation session1 = session(authToken, CREDS_1_USER_ID, creationTimestamp);
        Operation session2 = session(authToken2, CREDS_1_USER_ID, creationTimestamp);
        insertTestData(CREDS_1, session1, session2);

        sessionMapper.deleteSessions(CREDS_1_USER_ID, deletionTimestamp, PREFIX);

        assertEquals(2, countRowsInTableWhereColumnsEquals(SESSION,
                "user_id", quote(CREDS_1_USER_ID),
                "deletion_ts", deletionTimestamp.toEpochMilli())
        );
    }

    @Test
    public void shouldDeleteExpiredSessions() {
        String authToken = "id12345";
        String authToken2 = "id123456";
        Instant creationTimestamp = Instant.ofEpochSecond(31232133);
        Instant expirationTimestamp1 = Instant.ofEpochSecond(31232555);
        Instant expirationTimestamp2 = Instant.ofEpochSecond(31232777);
        Instant now = Instant.ofEpochSecond(31232666);

        Operation session1 = session(authToken, CREDS_1_USER_ID, creationTimestamp, expirationTimestamp1, null);
        Operation session2 = session(authToken2, CREDS_1_USER_ID, creationTimestamp, expirationTimestamp2, null);
        insertTestData(CREDS_1, session1, session2);

        sessionMapper.deleteExpiredSessions(now, PREFIX);

        assertEquals(1, countRowsInTableWhereColumnsEquals(SESSION,
                "user_id", quote(CREDS_1_USER_ID),
                "deletion_ts", now.toEpochMilli())
        );
    }

    @Test
    public void shouldPurgeDeletedSessions() {
        String authToken = "id12345";
        String authToken2 = "id123456";
        Instant creationTimestamp = Instant.ofEpochSecond(31232133);
        Instant deletionTimestamp1 = Instant.ofEpochSecond(31232555);
        Instant deletionTimestamp2 = deletionTimestamp1.plusSeconds(5000);
        Instant deletedBefore = deletionTimestamp1.plusSeconds(3600);

        Operation session1 = session(authToken, CREDS_1_USER_ID, creationTimestamp, null, deletionTimestamp1);
        Operation session2 = session(authToken2, CREDS_1_USER_ID, creationTimestamp, null, deletionTimestamp2);
        insertTestData(CREDS_1, session1, session2);

        sessionMapper.purgeDeletedSessions(deletedBefore, PREFIX);

        assertEquals(1, countRowsInTableWhereColumnsEquals(SESSION, "user_id", quote(CREDS_1_USER_ID)));
    }
}

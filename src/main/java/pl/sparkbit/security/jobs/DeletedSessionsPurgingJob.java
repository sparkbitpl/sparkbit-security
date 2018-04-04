package pl.sparkbit.security.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.sparkbit.security.dao.SessionDao;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static pl.sparkbit.security.config.Properties.*;

@Component
@ConditionalOnProperty(value = DELETED_SESSIONS_PURGING_ENABLED, havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class DeletedSessionsPurgingJob {

    private static final int ONE_WEEK_IN_MINUTES = 10_080;
    private static final int ONE_HOUR_IN_MILLIS = 3_600_000;

    private static final int DEFAULT_OLDER_THAN_MINUTES = ONE_WEEK_IN_MINUTES;
    private static final long DEFAULT_RUN_EVERY_MILLIS = ONE_HOUR_IN_MILLIS;

    private final Clock clock;
    private final SessionDao sessionDao;

    @Value("${" + DELETED_SESSIONS_PURGING_OLDER_THAN_MINUTES + ":" + DEFAULT_OLDER_THAN_MINUTES + "}")
    private int olderThanMinutes;

    @Scheduled(fixedDelayString = "${" + DELETED_SESSIONS_PURGING_RUN_EVERY_MILLIS + ":" + DEFAULT_RUN_EVERY_MILLIS
            + "}")
    @Transactional
    public void removeOldSessions() {
        log.trace("Purging expired sessions");
        Instant deletedBefore = clock.instant().minus(olderThanMinutes, ChronoUnit.MINUTES);
        sessionDao.purgeDeletedSessions(deletedBefore);
    }
}

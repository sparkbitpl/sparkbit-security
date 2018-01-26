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

import static pl.sparkbit.security.config.Properties.SESSION_DELETER_OLDER_THAN_MINUTES;
import static pl.sparkbit.security.config.Properties.SESSION_DELETER_REMOVE_OLD;
import static pl.sparkbit.security.config.Properties.SESSION_DELETER_RUN_EVERY_MILLIS;

@Component
@ConditionalOnProperty(value = SESSION_DELETER_REMOVE_OLD, havingValue = "true")
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class RemovingSessionsJob {

    private final Clock clock;
    private final SessionDao sessionDao;

    @Value("${" + SESSION_DELETER_OLDER_THAN_MINUTES + "}")
    private int olderThanMinutes;

    @Scheduled(fixedDelayString = "${" + SESSION_DELETER_RUN_EVERY_MILLIS + "}")
    @Transactional
    public void removeOldSessions() {
        Instant olderThan = clock.instant().minus(olderThanMinutes, ChronoUnit.MINUTES);
        sessionDao.deleteSessions(olderThan);
    }
}

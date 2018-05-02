package pl.sparkbit.security.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.sparkbit.security.dao.SessionDao;

import java.time.Clock;

import static pl.sparkbit.security.config.SecurityProperties.SESSION_EXPIRATION_ENABLED;

@Component
@ConditionalOnProperty(name = SESSION_EXPIRATION_ENABLED, havingValue = "true")
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class ExpiredSessionsDeletionJob {

    private static final int ONE_HOUR_IN_MILLIS = 3_600_000;

    private final Clock clock;
    private final SessionDao sessionDao;

    @Scheduled(fixedDelay = ONE_HOUR_IN_MILLIS)
    @Transactional
    public void removeOldSessions() {
        sessionDao.deleteExpiredSessions(clock.instant());
    }
}

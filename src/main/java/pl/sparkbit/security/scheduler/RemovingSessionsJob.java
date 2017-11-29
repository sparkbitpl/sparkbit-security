package pl.sparkbit.security.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.sparkbit.security.dao.SecurityDao;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@ConditionalOnProperty(value = "sparkbit.security.session.deleter.removeOld", havingValue = "true")
@Slf4j
@RequiredArgsConstructor
public class RemovingSessionsJob {

    private final Clock clock;
    private final SecurityDao securityDao;

    @Value("${sparkbit.security.session.deleter.olderThanMinutes}")
    private int olderThanMinutes;

    @Scheduled(fixedDelayString = "${sparkbit.security.session.deleter.runEveryMillis}")
    @Transactional
    public void removeOldSessions() {
        Instant olderThan = clock.instant().minus(olderThanMinutes, ChronoUnit.MINUTES);
        securityDao.deleteSessions(olderThan);
    }

}

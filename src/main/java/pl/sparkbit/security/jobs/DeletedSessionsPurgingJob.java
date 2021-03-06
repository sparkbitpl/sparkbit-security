package pl.sparkbit.security.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.sparkbit.security.config.SecurityProperties;
import pl.sparkbit.security.dao.SessionDao;

import java.time.Clock;
import java.time.Instant;

import static pl.sparkbit.security.config.SecurityProperties.*;

@Component
@ConditionalOnProperty(value = DELETED_SESSIONS_PURGING_ENABLED, havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class DeletedSessionsPurgingJob {

    private final Clock clock;
    private final SessionDao sessionDao;
    private final SecurityProperties configuration;

    @Scheduled(fixedDelayString = "#{securityProperties.getDeletedSessionPurging().getRunEvery()}")
    @Transactional
    public void removeOldSessions() {
        log.trace("Purging expired sessions");
        Instant deletedBefore = clock.instant().minus(configuration.getDeletedSessionPurging().getOlderThan());
        sessionDao.purgeDeletedSessions(deletedBefore);
    }
}

package pl.sparkbit.security.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.sparkbit.security.dao.SecurityChallengeDao;

import java.time.Clock;

import static pl.sparkbit.security.config.Properties.EXPIRED_CHALLENGE_DELETION_RUN_EVERY_MILLIS;

@Component
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class ExpiredChallengesDeletionJob {

    private static final int ONE_HOUR_IN_MILLIS = 3_600_000;

    private final SecurityChallengeDao securityChallengeDao;
    private final Clock clock;

    @Scheduled(fixedDelayString = "${" + EXPIRED_CHALLENGE_DELETION_RUN_EVERY_MILLIS + ":" + ONE_HOUR_IN_MILLIS + "}")
    @Transactional
    public void removeExpiredChallenges() {
        log.trace("Deleting expired challenges");
        securityChallengeDao.deleteExpiredChallenges(clock.instant());
    }
}

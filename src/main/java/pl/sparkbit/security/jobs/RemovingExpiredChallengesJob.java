package pl.sparkbit.security.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.sparkbit.security.dao.SecurityChallengeDao;

import java.time.Clock;

import static pl.sparkbit.security.Properties.CHALLENGE_DELETER_RUN_EVERY_MILLIS;

@Component
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class RemovingExpiredChallengesJob {

    private final SecurityChallengeDao securityChallengeDao;
    private final Clock clock;

    @Scheduled(fixedDelayString = "${" + CHALLENGE_DELETER_RUN_EVERY_MILLIS + ":3600000}")
    @Transactional
    public void removeExpiredChallenges() {
        log.trace("Removing expired challenges");
        securityChallengeDao.deleteExpiredChallenges(clock.instant());
    }
}

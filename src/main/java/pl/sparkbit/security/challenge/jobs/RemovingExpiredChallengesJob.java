package pl.sparkbit.security.challenge.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.sparkbit.security.challenge.dao.SecurityChallengeDao;

import java.time.Clock;

@Component
@Slf4j
@RequiredArgsConstructor
public class RemovingExpiredChallengesJob {

    private final SecurityChallengeDao securityChallengeDao;
    private final Clock clock;

    @Scheduled(fixedDelayString = "${sparkbit.security.challenge.deleter.runEveryMillis:3600000}")
    @Transactional
    public void removeExpiredChallenges() {
        log.trace("Removing expired challenges");
        securityChallengeDao.deleteExpiredChallenges(clock.instant());
    }
}

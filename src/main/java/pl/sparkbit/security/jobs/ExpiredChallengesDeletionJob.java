package pl.sparkbit.security.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.sparkbit.security.dao.SecurityChallengeDao;

import java.time.Clock;

@Component
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class ExpiredChallengesDeletionJob {

    private final SecurityChallengeDao securityChallengeDao;
    private final Clock clock;

    @Scheduled(fixedDelayString = "#{securityProperties.getExpiredChallengeDeletion().getRunEvery()}")
    @Transactional
    public void removeExpiredChallenges() {
        log.trace("Deleting expired challenges");
        securityChallengeDao.deleteExpiredChallenges(clock.instant());
    }
}

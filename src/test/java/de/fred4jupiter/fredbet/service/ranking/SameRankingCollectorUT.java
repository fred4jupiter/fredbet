package de.fred4jupiter.fredbet.service.ranking;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.repository.UsernamePoints;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
public class SameRankingCollectorUT {

    @InjectMocks
    private SameRankingCollector sameRankingCollector;

    @Test
    public void markEntriesWithSameRanking() {
        List<UsernamePoints> list = Arrays.asList(
                createFor(10, 2, 170),
                createFor(10, 2, 170),
                createFor(10, 2, 170),
                createFor(10, 2, 171));

        sameRankingCollector.markEntriesWithSameRanking(list);

        int sameResult = countSameResult(list);
        assertThat(sameResult).isEqualTo(3);
    }

    private static UsernamePoints createFor(int totalPoints, int correctResults, int goalDifference) {
        UsernamePoints usernamePoints = new UsernamePoints();
        usernamePoints.setTotalPoints(totalPoints);
        usernamePoints.setCorrectResultCount(correctResults);
        usernamePoints.setGoalDifference(goalDifference);
        return usernamePoints;
    }

    private int countSameResult(List<UsernamePoints> list) {
        final AtomicInteger counter = new AtomicInteger();

        list.forEach(usernamePoints -> {
            if (usernamePoints.isSameRankingPositionAsOtherUser()) {
                counter.incrementAndGet();
            }
        });
        return counter.get();
    }
}

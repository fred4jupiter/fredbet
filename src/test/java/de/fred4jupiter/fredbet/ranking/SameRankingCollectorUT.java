package de.fred4jupiter.fredbet.ranking;

import de.fred4jupiter.fredbet.common.UnitTest;
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
                createFor("user1", 10, 2, 170),
                createFor("user2", 10, 2, 170),
                createFor("user3", 10, 2, 170),
                createFor("user4", 10, 2, 171));

        sameRankingCollector.markEntriesWithSameRanking(list);

        int sameResult = countSameResult(list);
        assertThat(sameResult).isEqualTo(3);
    }

    @Test
    public void markEntriesWithSameRankingAllDifferent() {
        List<UsernamePoints> list = Arrays.asList(
                createFor("user1", 1, 4, 170),
                createFor("user2", 2, 3, 170),
                createFor("user3", 3, 2, 170),
                createFor("user4", 4, 1, 171));

        sameRankingCollector.markEntriesWithSameRanking(list);

        int sameResult = countSameResult(list);
        assertThat(sameResult).isEqualTo(0);
    }

    @Test
    public void assignRankPositionsSharesPositionForSameRanking() {
        List<UsernamePoints> list = Arrays.asList(
                createFor("user1", 10, 3, 170),
                createFor("user2", 8, 2, 170),
                createFor("user3", 8, 2, 170),
                createFor("user4", 5, 1, 171));

        sameRankingCollector.assignRankPositions(list);

        assertThat(list).extracting(UsernamePoints::getRankPosition).containsExactly(1, 2, 2, 4);
    }

    private UsernamePoints createFor(String username, int totalPoints, int correctResults, int goalDifference) {
        UsernamePoints usernamePoints = new UsernamePoints(username, totalPoints);
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

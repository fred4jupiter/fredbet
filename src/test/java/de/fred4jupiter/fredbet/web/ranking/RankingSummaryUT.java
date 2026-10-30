package de.fred4jupiter.fredbet.web.ranking;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.ranking.UsernamePoints;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
public class RankingSummaryUT {

    // anna leads, bert and admin share the second place, carl is last
    private final List<UsernamePoints> rankings = List.of(
        ranking("anna", 10, 1), ranking("bert", 8, 2), ranking("admin", 8, 2), ranking("carl", 5, 4));

    @Test
    public void summaryOfLeader() {
        RankingSummary summary = RankingSummary.of(rankings, "anna");

        assertThat(summary.leading()).isTrue();
        assertThat(summary.pointsBehindLeader()).isZero();
        assertThat(summary.nextPosition()).isEqualTo(2);
        assertThat(summary.pointsAheadOfNext()).isEqualTo(2);
    }

    @Test
    public void usersWithTheSamePositionAreSkippedForTheNextPosition() {
        RankingSummary summary = RankingSummary.of(rankings, "bert");

        assertThat(summary.position()).isEqualTo(2);
        assertThat(summary.points()).isEqualTo(8);
        assertThat(summary.leading()).isFalse();
        assertThat(summary.pointsBehindLeader()).isEqualTo(2);
        // not admin on the same place 2, but carl on place 4
        assertThat(summary.nextPosition()).isEqualTo(4);
        assertThat(summary.pointsAheadOfNext()).isEqualTo(3);
    }

    @Test
    public void lastUserHasNoNextPosition() {
        RankingSummary summary = RankingSummary.of(rankings, "carl");

        assertThat(summary.pointsBehindLeader()).isEqualTo(5);
        assertThat(summary.nextPosition()).isNull();
        assertThat(summary.pointsAheadOfNext()).isNull();
    }

    @Test
    public void userNotInTheRankingHasNoSummary() {
        assertThat(RankingSummary.of(rankings, "dora")).isNull();
        assertThat(RankingSummary.of(List.of(), "anna")).isNull();
    }

    private UsernamePoints ranking(String userName, int points, int position) {
        UsernamePoints usernamePoints = new UsernamePoints(userName, points);
        usernamePoints.setRankPosition(position);
        return usernamePoints;
    }
}

package de.fred4jupiter.fredbet.ranking;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.entity.Bet;
import de.fred4jupiter.fredbet.domain.entity.Match;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@UnitTest
public class RankingVisitorsUT {

    @Test
    public void correctResultVisitorCountsOnlyExactResultsPerUser() {
        CorrectResultVisitor visitor = new CorrectResultVisitor();
        visitor.visit(createBet("alfredo", 2, 1, 2, 1));
        visitor.visit(createBet("alfredo", 1, 1, 1, 1));
        visitor.visit(createBet("bea", 2, 1, 0, 1));

        assertThat(visitor.getTotalCorrectResultCountForUser("alfredo")).isEqualTo(2);
        assertThat(visitor.getTotalCorrectResultCountForUser("bea")).isZero();
    }

    @Test
    public void goalDifferenceVisitorAccumulatesAbsoluteDifferencesPerUser() {
        GoalDifferenceVisitor visitor = new GoalDifferenceVisitor();
        visitor.visit(createBet("alfredo", 2, 0, 1, 0));
        visitor.visit(createBet("alfredo", 3, 3, 2, 1));

        assertThat(visitor.getTotalGoalDifferenceForUser("alfredo")).isEqualTo(4);
    }

    @Test
    public void goalDifferenceVisitorRejectsMissingResults() {
        GoalDifferenceVisitor visitor = new GoalDifferenceVisitor();
        Bet bet = new Bet();
        bet.setUserName("alfredo");
        bet.setGoalsTeamOne(null);
        bet.setGoalsTeamTwo(1);
        Match match = new Match();
        match.setGoalsTeamOne(1);
        match.setGoalsTeamTwo(1);
        bet.setMatch(match);

        assertThatThrownBy(() -> visitor.visit(bet)).isInstanceOf(IllegalStateException.class);
    }

    private Bet createBet(String userName, Integer betTeamOne, Integer betTeamTwo, Integer matchTeamOne, Integer matchTeamTwo) {
        Bet bet = new Bet();
        bet.setUserName(userName);
        bet.setGoalsTeamOne(betTeamOne);
        bet.setGoalsTeamTwo(betTeamTwo);

        Match match = new Match();
        match.setGoalsTeamOne(matchTeamOne);
        match.setGoalsTeamTwo(matchTeamTwo);
        bet.setMatch(match);
        return bet;
    }
}


package de.fred4jupiter.fredbet.web.matches;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.builder.BetBuilder;
import de.fred4jupiter.fredbet.domain.builder.MatchBuilder;
import de.fred4jupiter.fredbet.domain.entity.Bet;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.team.TeamService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;

@UnitTest
public class PointsBadgeUT {

    @Mock
    private TeamService teamService;

    @Test
    public void pendingAsLongAsMatchHasNoResult() {
        Match match = MatchBuilder.create(teamService).withGroup(Group.GROUP_A).build();
        Bet bet = BetBuilder.create().withGoals(1, 2).build();

        assertEquals(new PointsBadge("points-pending", "matches.list.points.pending", "–", false), PointsBadge.of(match, bet));
    }

    @Test
    public void outcomeOfFinishedMatchWithJoker() {
        Match match = MatchBuilder.create(teamService).withGoals(2, 1).withGroup(Group.GROUP_A).build();
        Bet bet = BetBuilder.create().withGoals(3, 2).withJoker(true).withPoints(4).build();

        assertEquals(new PointsBadge("points-same-goal-difference", "points.config.pointsSameGoalDifference", "+4", true), PointsBadge.of(match, bet));
    }

    @Test
    public void noPointsWithoutBetOnFinishedMatch() {
        Match match = MatchBuilder.create(teamService).withGoals(2, 1).withGroup(Group.GROUP_A).build();

        assertEquals(new PointsBadge("points-none", "matches.list.points.none", "0", false), PointsBadge.of(match, null));
    }

    @Test
    public void outcomeWithoutPointsIsShownAsNoPoints() {
        Match match = MatchBuilder.create(teamService).withGoals(2, 1).withGroup(Group.GROUP_A).build();
        Bet bet = BetBuilder.create().withGoals(2, 3).withJoker(true).withPoints(0).build();

        assertEquals(new PointsBadge("points-none", "matches.list.points.none", "0", false), PointsBadge.of(match, bet));
    }
}

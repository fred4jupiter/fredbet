package de.fred4jupiter.fredbet.standings;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.entity.Team;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
public class TeamStandingsUT {

    @Test
    public void shouldCalculateStandingsForWinDrawAndLoss() {
        Team team = new Team();
        team.setCountry(Country.GERMANY);
        team.setName("Germany");

        TeamStandings teamStandings = new TeamStandings(team);
        teamStandings.addMatch(2, 1);
        teamStandings.addMatch(1, 1);
        teamStandings.addMatch(0, 3);

        assertThat(teamStandings.getTeam()).isEqualTo(team);
        assertThat(teamStandings.getNumberOfMatches()).isEqualTo(3);
        assertThat(teamStandings.getNumberOfWins()).isEqualTo(1);
        assertThat(teamStandings.getNumberOfUndecided()).isEqualTo(1);
        assertThat(teamStandings.getNumberOfLooses()).isEqualTo(1);
        assertThat(teamStandings.getNumberOfGoals()).isEqualTo(3);
        assertThat(teamStandings.getNumberOfGoalsAgainst()).isEqualTo(5);
        assertThat(teamStandings.getNumberOfGoalDifference()).isEqualTo(-2);
        assertThat(teamStandings.getNumberOfPoints()).isEqualTo(4);
    }

    @Test
    public void shouldReturnCssClassDependingOnRowCount() {
        TeamStandings teamStandings = new TeamStandings(new Team());

        assertThat(teamStandings.getCssClass(1)).isEqualTo("success");
        assertThat(teamStandings.getCssClass(2)).isEqualTo("success");
        assertThat(teamStandings.getCssClass(3)).isEqualTo("warning");
        assertThat(teamStandings.getCssClass(4)).isEmpty();
    }
}


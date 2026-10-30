package de.fred4jupiter.fredbet.standings;

import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.domain.entity.Team;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class StandingsContainerUT {

    @Test
    public void noStandingsWithoutResults() {
        assertThat(new StandingsContainer().hasStandings()).isFalse();
    }

    @Test
    public void knockoutMatchesDoNotCreateStandings() {
        StandingsContainer standingsContainer = new StandingsContainer();
        standingsContainer.registerResult(match(Group.ROUND_OF_SIXTEEN));

        assertThat(standingsContainer.hasStandings()).isFalse();
    }

    @Test
    public void groupMatchCreatesStandings() {
        StandingsContainer standingsContainer = new StandingsContainer();
        standingsContainer.registerResult(match(Group.GROUP_A));

        assertThat(standingsContainer.hasStandings()).isTrue();
        assertThat(standingsContainer.getForGroup(Group.GROUP_A)).hasSize(2);
    }

    private Match match(Group group) {
        Match match = new Match();
        match.setGroup(group);
        match.setTeamOne(team(1L, "FC One"));
        match.setTeamTwo(team(2L, "FC Two"));
        match.setGoalsTeamOne(2);
        match.setGoalsTeamTwo(1);
        return match;
    }

    private Team team(Long id, String name) {
        Team team = new Team(name);
        ReflectionTestUtils.setField(team, "id", id);
        return team;
    }
}

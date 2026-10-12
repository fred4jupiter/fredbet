package de.fred4jupiter.fredbet.data;

import de.fred4jupiter.fredbet.betting.BettingService;
import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.team.TeamService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@UnitTest
public class DataPopulatorUT {

    @Mock
    private TeamService teamService;

    @Mock
    private MatchService matchService;

    @Mock
    private BettingService bettingService;

    @InjectMocks
    private DataPopulator dataPopulator;

    @Test
    public void hasEnoughTeamsForDemoData_withoutTeams_returnsFalse() {
        when(teamService.findAllSelectableTeams()).thenReturn(List.of());

        assertThat(dataPopulator.hasEnoughTeamsForDemoData()).isFalse();
    }

    @Test
    public void hasEnoughTeamsForDemoData_withTwoTeams_returnsTrue() {
        when(teamService.findAllSelectableTeams()).thenReturn(List.of(new Team(Country.GERMANY), new Team(Country.FRANCE)));

        assertThat(dataPopulator.hasEnoughTeamsForDemoData()).isTrue();
    }

    @Test
    public void createDemoData_withOnlyOneTeam_throwsExceptionAndKeepsExistingMatches() {
        when(teamService.findAllSelectableTeams()).thenReturn(List.of(new Team(Country.GERMANY)));

        assertThatThrownBy(() -> dataPopulator.createDemoData()).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(matchService, bettingService);
    }
}

package de.fred4jupiter.fredbet.web.info;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.standings.StandingsContainer;
import de.fred4jupiter.fredbet.standings.StandingsService;
import de.fred4jupiter.fredbet.standings.TeamStandings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MockMvcIntegrationTest
@WithUserDetails("admin")
public class StandingsControllerIT {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private StandingsService standingsService;

    @Test
    public void showStandingsWithHighlightedRowsAndTranslatedHeaders() {
        StandingsContainer standingsContainer = mock(StandingsContainer.class);
        when(standingsContainer.hasStandings()).thenReturn(true);
        when(standingsContainer.getGroups()).thenReturn(List.of(Group.GROUP_A));
        when(standingsContainer.getForGroup(any())).thenReturn(List.of());
        when(standingsContainer.getForGroup(Group.GROUP_A)).thenReturn(List.of(
            standings(1L, "FC One", 3, 0), standings(2L, "FC Two", 2, 1), standings(3L, "FC Three", 1, 2), standings(4L, "FC Four", 0, 3)));
        when(standingsService.calculateStandings()).thenReturn(standingsContainer);

        assertThat(mvc.get().uri("/standings"))
            .hasStatusOk()
            .bodyText().contains(
                "FC Four",
                "/team/4.svg",
                "Places 1–2: qualified for the knockout stage",
                "class=\"table-success\"",
                "class=\"table-warning\"",
                "<abbr title=\"Goal difference\">GD</abbr>",
                "Losses");
    }

    @Test
    public void showNoDataMessageWithoutResults() {
        when(standingsService.calculateStandings()).thenReturn(new StandingsContainer());

        assertThat(mvc.get().uri("/standings"))
            .hasStatusOk()
            .bodyText()
            .contains("No results available yet.")
            .doesNotContain("standings-table");
    }

    private TeamStandings standings(Long teamId, String teamName, int scored, int conceded) {
        Team team = new Team(teamName);
        ReflectionTestUtils.setField(team, "id", teamId);
        TeamStandings teamStandings = new TeamStandings(team);
        teamStandings.addMatch(scored, conceded);
        return teamStandings;
    }
}

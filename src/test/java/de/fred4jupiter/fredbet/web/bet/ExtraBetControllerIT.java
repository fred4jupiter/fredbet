package de.fred4jupiter.fredbet.web.bet;

import de.fred4jupiter.fredbet.betting.ExtraBettingService;
import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.domain.entity.ExtraBet;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.team.TeamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@MockMvcIntegrationTest
@WithUserDetails("admin")
public class ExtraBetControllerIT {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private ExtraBettingService extraBettingService;

    @MockitoBean
    private TeamService teamService;

    @MockitoBean
    private MatchService matchService;

    @Test
    public void showSelectedTeamsWithCrests() {
        Team one = team(11L, "FC One");
        Team two = team(12L, "FC Two");
        when(teamService.findAllSelectableTeamsOfMatches()).thenReturn(List.of(one, two));
        when(matchService.isGameForThirdAvailable()).thenReturn(false);

        ExtraBet extraBet = new ExtraBet();
        extraBet.setUserName("admin");
        extraBet.setFinalWinner(two);
        when(extraBettingService.loadExtraBetForUser(any())).thenReturn(extraBet);

        assertThat(mvc.get().uri("/extrabets"))
            .hasStatusOk()
            .bodyText()
            // the crest of the selected champion is shown, the vice champion is not selected yet
            .contains(
                "id=\"finalWinnerCrest\"",
                "src=\"/team/12.svg",
                "data-crest=\"/team/11.svg",
                "id=\"semiFinalWinnerCrest\"",
                "extra-medal-2")
            .doesNotContain("extra-medal-3");
    }

    private Team team(Long id, String name) {
        Team team = new Team(name);
        ReflectionTestUtils.setField(team, "id", id);
        return team;
    }

}

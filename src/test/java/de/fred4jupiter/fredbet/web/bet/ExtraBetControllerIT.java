package de.fred4jupiter.fredbet.web.bet;

import de.fred4jupiter.fredbet.betting.ExtraBettingService;
import de.fred4jupiter.fredbet.common.LoggedInSession;
import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.domain.entity.ExtraBet;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.team.TeamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.List;
import java.util.Locale;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcIntegrationTest
public class ExtraBetControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDetailsService userDetailsService;

    @MockitoBean
    private ExtraBettingService extraBettingService;

    @MockitoBean
    private TeamService teamService;

    @MockitoBean
    private MatchService matchService;

    @Test
    public void showSelectedTeamsWithCrests() throws Exception {
        Team one = team(11L, "FC One");
        Team two = team(12L, "FC Two");
        when(teamService.findAllSelectableTeamsOfMatches()).thenReturn(List.of(one, two));
        when(matchService.isGameForThirdAvailable()).thenReturn(false);

        ExtraBet extraBet = new ExtraBet();
        extraBet.setUserName("admin");
        extraBet.setFinalWinner(two);
        when(extraBettingService.loadExtraBetForUser(any())).thenReturn(extraBet);

        mockMvc.perform(get("/extrabets").session(englishSession()))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                // the crest of the selected champion is shown, the vice champion is not selected yet
                containsString("id=\"finalWinnerCrest\""),
                containsString("src=\"/team/12.svg"),
                containsString("data-crest=\"/team/11.svg"),
                containsString("id=\"semiFinalWinnerCrest\""),
                containsString("extra-medal-2"),
                not(containsString("extra-medal-3")))));
    }

    private Team team(Long id, String name) {
        Team team = new Team(name);
        ReflectionTestUtils.setField(team, "id", id);
        return team;
    }

    private MockHttpSession englishSession() {
        MockHttpSession session = LoggedInSession.of(userDetailsService, "admin");
        session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, Locale.ENGLISH);
        return session;
    }
}

package de.fred4jupiter.fredbet.web.info;

import de.fred4jupiter.fredbet.common.LoggedInSession;
import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.standings.StandingsContainer;
import de.fred4jupiter.fredbet.standings.StandingsService;
import de.fred4jupiter.fredbet.standings.TeamStandings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.List;
import java.util.Locale;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcIntegrationTest
public class StandingsControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDetailsService userDetailsService;

    @MockitoBean
    private StandingsService standingsService;

    @Test
    public void showStandingsWithHighlightedRowsAndTranslatedHeaders() throws Exception {
        StandingsContainer standingsContainer = mock(StandingsContainer.class);
        when(standingsContainer.getGroups()).thenReturn(List.of(Group.GROUP_A));
        when(standingsContainer.getForGroup(any())).thenReturn(List.of());
        when(standingsContainer.getForGroup(Group.GROUP_A)).thenReturn(List.of(
            standings("FC One", 3, 0), standings("FC Two", 2, 1), standings("FC Three", 1, 2), standings("FC Four", 0, 3)));
        when(standingsService.calculateStandings()).thenReturn(standingsContainer);

        MockHttpSession session = LoggedInSession.of(userDetailsService, "admin");
        session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, Locale.ENGLISH);

        mockMvc.perform(get("/standings").session(session))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("FC Four"),
                containsString("class=\"table-success\""),
                containsString("class=\"table-warning\""),
                containsString("<abbr title=\"Goal difference\">GD</abbr>"),
                containsString("Losses"))));
    }

    private TeamStandings standings(String teamName, int scored, int conceded) {
        TeamStandings teamStandings = new TeamStandings(new Team(teamName));
        teamStandings.addMatch(scored, conceded);
        return teamStandings;
    }
}

package de.fred4jupiter.fredbet.web.info;

import de.fred4jupiter.fredbet.common.LoggedInSession;
import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.statistic.Statistic;
import de.fred4jupiter.fredbet.statistic.StatisticService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcIntegrationTest
public class StatisticControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDetailsService userDetailsService;

    @MockitoBean
    private StatisticService statisticService;

    @Test
    public void showStatisticTableWithFavouriteTeamColumn() throws Exception {
        Team favouriteTeam = new Team("FC Favourite");
        ReflectionTestUtils.setField(favouriteTeam, "id", 7L);

        Statistic anna = new Statistic("anna");
        anna.setPointsGroup(5);
        anna.setPointsForExtraBets(3);
        anna.setPointsFavouriteTeam(2);
        anna.setMaxPointsCandidate(true);
        Statistic bert = new Statistic("bert");
        bert.setPointsGroup(1);
        bert.setMinPointsCandidate(true);

        when(statisticService.getFavouriteTeam()).thenReturn(favouriteTeam);
        when(statisticService.createStatistic()).thenReturn(List.of(anna, bert));

        mockMvc.perform(get("/info/statistic").session(englishSession()))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("statistic-table"),
                containsString("anna"),
                containsString("bert"),
                containsString("/team/7.svg"),
                containsString("<abbr title=\"Quarter Final\">1/4</abbr>"),
                containsString("Total"),
                containsString("Flop-Better"),
                containsString("already included in the phase points"))));
    }

    @Test
    public void showNoDataMessageWithoutStatistics() throws Exception {
        when(statisticService.createStatistic()).thenReturn(List.of());

        mockMvc.perform(get("/info/statistic").session(englishSession()))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("There are no statistics by now!"),
                not(containsString("statistic-table")))));
    }

    private MockHttpSession englishSession() {
        MockHttpSession session = LoggedInSession.of(userDetailsService, "admin");
        session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, Locale.ENGLISH);
        return session;
    }
}

package de.fred4jupiter.fredbet.web.bet;

import de.fred4jupiter.fredbet.betting.ExtraBettingService;
import de.fred4jupiter.fredbet.common.LoggedInSession;
import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.domain.entity.ExtraBet;
import de.fred4jupiter.fredbet.domain.entity.Team;
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
public class ExtraBetOthersControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDetailsService userDetailsService;

    @MockitoBean
    private ExtraBettingService extraBettingService;

    @Test
    public void showPicksOfAllUsersInOneTable() throws Exception {
        ExtraBet anna = extraBet("anna", team(1L, "FC One"), team(2L, "FC Two"), null);
        anna.setPointsOne(10);
        ExtraBet admin = extraBet("admin", team(2L, "FC Two"), team(1L, "FC One"), null);
        when(extraBettingService.loadExtraBetDataOthers()).thenReturn(List.of(anna, admin));

        mockMvc.perform(get("/extrabets/others").session(englishSession()))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("extra-others-table"),
                containsString("/team/1.svg"),
                containsString("table-success"),
                containsString("extra-others-current-user"),
                // nobody picked a third place, so the column is hidden
                not(containsString("extra-medal-3")))));
    }

    @Test
    public void showThirdPlaceColumnIfPicked() throws Exception {
        ExtraBet anna = extraBet("anna", team(1L, "FC One"), team(2L, "FC Two"), team(3L, "FC Three"));
        when(extraBettingService.loadExtraBetDataOthers()).thenReturn(List.of(anna));

        mockMvc.perform(get("/extrabets/others").session(englishSession()))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("extra-medal-3"),
                containsString("/team/3.svg"))));
    }

    @Test
    public void showMessageWithoutExtraBets() throws Exception {
        when(extraBettingService.loadExtraBetDataOthers()).thenReturn(List.of());

        mockMvc.perform(get("/extrabets/others").session(englishSession()))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("No bets assigned!"),
                not(containsString("extra-others-table")))));
    }

    private ExtraBet extraBet(String userName, Team finalWinner, Team semiFinalWinner, Team thirdFinalWinner) {
        ExtraBet extraBet = new ExtraBet();
        extraBet.setUserName(userName);
        extraBet.setFinalWinner(finalWinner);
        extraBet.setSemiFinalWinner(semiFinalWinner);
        extraBet.setThirdFinalWinner(thirdFinalWinner);
        return extraBet;
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

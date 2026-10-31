package de.fred4jupiter.fredbet.web.bet;

import de.fred4jupiter.fredbet.betting.ExtraBettingService;
import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.domain.entity.ExtraBet;
import de.fred4jupiter.fredbet.domain.entity.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@MockMvcIntegrationTest
@WithUserDetails("admin")
public class ExtraBetOthersControllerIT {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private ExtraBettingService extraBettingService;

    @Test
    public void showPicksOfAllUsersInOneTable() {
        ExtraBet anna = extraBet("anna", team(1L, "FC One"), team(2L, "FC Two"), null);
        anna.setPointsOne(10);
        ExtraBet admin = extraBet("admin", team(2L, "FC Two"), team(1L, "FC One"), null);
        when(extraBettingService.loadExtraBetDataOthers()).thenReturn(List.of(anna, admin));

        assertThat(mvc.get().uri("/extrabets/others"))
            .hasStatusOk()
            .bodyText()
            .contains(
                "extra-others-table",
                "/team/1.svg",
                "table-success",
                "extra-others-current-user")
            // nobody picked a third place, so the column is hidden
            .doesNotContain("extra-medal-3");
    }

    @Test
    public void showThirdPlaceColumnIfPicked() {
        ExtraBet anna = extraBet("anna", team(1L, "FC One"), team(2L, "FC Two"), team(3L, "FC Three"));
        when(extraBettingService.loadExtraBetDataOthers()).thenReturn(List.of(anna));

        assertThat(mvc.get().uri("/extrabets/others"))
            .hasStatusOk()
            .bodyText().contains("extra-medal-3", "/team/3.svg");
    }

    @Test
    public void showMessageWithoutExtraBets() {
        when(extraBettingService.loadExtraBetDataOthers()).thenReturn(List.of());

        assertThat(mvc.get().uri("/extrabets/others"))
            .hasStatusOk()
            .bodyText()
            .contains("No bets assigned!")
            .doesNotContain("extra-others-table");
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

}

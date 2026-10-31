package de.fred4jupiter.fredbet.web.info;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.statistic.Statistic;
import de.fred4jupiter.fredbet.statistic.StatisticService;
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
public class StatisticControllerIT {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private StatisticService statisticService;

    @Test
    public void showStatisticTableWithFavouriteTeamColumn() {
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

        assertThat(mvc.get().uri("/info/statistic"))
            .hasStatusOk()
            .bodyText().contains(
                "statistic-table",
                "anna",
                "bert",
                "/team/7.svg",
                "<abbr title=\"Quarter Final\">1/4</abbr>",
                "Total",
                "Flop-Better",
                "already included in the phase points");
    }

    @Test
    public void showNoDataMessageWithoutStatistics() {
        when(statisticService.createStatistic()).thenReturn(List.of());

        assertThat(mvc.get().uri("/info/statistic"))
            .hasStatusOk()
            .bodyText()
            .contains("There are no statistics by now!")
            .doesNotContain("statistic-table");
    }

}

package de.fred4jupiter.fredbet.web.ranking;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.domain.RankingSelection;
import de.fred4jupiter.fredbet.ranking.RankingService;
import de.fred4jupiter.fredbet.ranking.UsernamePoints;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@MockMvcIntegrationTest
@WithUserDetails("admin")
public class RankingControllerIT {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private RankingService rankingService;

    @Test
    public void usersWithSameRankingShareThePosition() {
        UsernamePoints anna = new UsernamePoints("anna", 10);
        UsernamePoints bert = new UsernamePoints("bert", 8);
        UsernamePoints admin = new UsernamePoints("admin", 8);
        UsernamePoints carl = new UsernamePoints("carl", 5);
        bert.setSameRankingPositionAsOtherUser(true);
        admin.setSameRankingPositionAsOtherUser(true);
        // the positions are calculated by the (mocked) ranking service
        anna.setRankPosition(1);
        bert.setRankPosition(2);
        admin.setRankPosition(2);
        carl.setRankPosition(4);
        when(rankingService.calculateCurrentRanking(RankingSelection.MIXED)).thenReturn(List.of(anna, bert, admin, carl));

        MvcTestResult result = mvc.get().uri("/ranking").exchange();

        assertThat(result).hasStatusOk()
            .model().containsEntry("rankingSummary", new RankingSummary(2, 8, 2, 4, 3));
        assertThat(result).bodyText()
            .contains(
                "ranking-medal-1",
                "ranking-medal-2\">2</span>",
                "ranking-current-user ranking-shared-position",
                "Same position as other users",
                "Correct Result Bets: 0, Goal Difference: 0")
            .doesNotContain("ranking-medal-4");
    }

    @Test
    public void showDetailColumnsAndOwnPositionInSidebar() {
        UsernamePoints anna = new UsernamePoints("anna", 10);
        UsernamePoints admin = new UsernamePoints("admin", 8);
        anna.setRankPosition(1);
        admin.setRankPosition(2);
        admin.setCorrectResultCount(3);
        admin.setGoalDifference(5);
        when(rankingService.calculateCurrentRanking(RankingSelection.MIXED)).thenReturn(List.of(anna, admin));

        assertThat(mvc.get().uri("/ranking"))
            .hasStatusOk()
            .bodyText()
            .contains(
                "Correct results",
                "Goal difference",
                "title=\"Correct results\">3</span>",
                "title=\"Goal difference\">5</span>",
                "Your position",
                "Place 2",
                "Points: 8",
                "Gap to the leader: 2")
            .doesNotContain("Lead over place");
    }

    @Test
    public void showHintIfCurrentUserIsNotRanked() {
        UsernamePoints anna = new UsernamePoints("anna", 10);
        anna.setRankPosition(1);
        when(rankingService.calculateCurrentRanking(RankingSelection.MIXED)).thenReturn(List.of(anna));

        MvcTestResult result = mvc.get().uri("/ranking").exchange();

        assertThat(result).hasStatusOk().model().extractingByKey("rankingSummary").isNull();
        assertThat(result).bodyText().contains("You are not in this ranking yet.");
    }

    @Test
    public void showNoRankingMessageWithoutBets() {
        when(rankingService.calculateCurrentRanking(RankingSelection.MIXED)).thenReturn(List.of());

        assertThat(mvc.get().uri("/ranking"))
            .hasStatusOk()
            .bodyText().contains("No bets assigned!");
    }

}

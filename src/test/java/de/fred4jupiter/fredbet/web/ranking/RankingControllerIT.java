package de.fred4jupiter.fredbet.web.ranking;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.domain.RankingSelection;
import de.fred4jupiter.fredbet.ranking.RankingService;
import de.fred4jupiter.fredbet.ranking.UsernamePoints;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcIntegrationTest
@WithUserDetails("admin")
public class RankingControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RankingService rankingService;

    @Test
    public void usersWithSameRankingShareThePosition() throws Exception {
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

        MvcResult result = mockMvc.perform(get("/ranking"))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("ranking-medal-1"),
                containsString("ranking-current-user ranking-shared-position"),
                containsString("Same position as other users"),
                containsString("Correct Result Bets: 0, Goal Difference: 0"))))
            .andReturn();

        String html = result.getResponse().getContentAsString();
        assertThat(html).contains("ranking-medal-2\">2</span>").doesNotContain("ranking-medal-4");
    }

    @Test
    public void showNoRankingMessageWithoutBets() throws Exception {
        when(rankingService.calculateCurrentRanking(RankingSelection.MIXED)).thenReturn(List.of());

        mockMvc.perform(get("/ranking"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("No bets assigned!")));
    }

}

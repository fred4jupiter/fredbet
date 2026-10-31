package de.fred4jupiter.fredbet.web.matches;

import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import de.fred4jupiter.fredbet.betting.BettingService;
import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.entity.Bet;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.match.MatchService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@MockMvcIntegrationTest
@WithUserDetails("admin")
public class MyBetsPdfExportIT {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private MatchService matchService;

    @MockitoBean
    private BettingService bettingService;

    @Test
    public void exportAllMatchesWithOwnBetsAsA4Pdf() throws Exception {
        LocalDateTime kickOff = LocalDateTime.now().minusDays(30);
        List<Match> matches = new ArrayList<>();
        for (long i = 1; i <= 64; i++) {
            matches.add(match(i, kickOff.plusHours(i * 6), "Team" + i + "A", "Team" + i + "B"));
        }
        // a finished match with a correct joker bet and an open match without bet
        Match finished = matches.get(0);
        finished.setGoalsTeamOne(2);
        finished.setGoalsTeamTwo(1);
        Bet bet = new Bet();
        bet.setUserName("admin");
        bet.setMatch(finished);
        bet.setGoalsTeamOne(2);
        bet.setGoalsTeamTwo(1);
        bet.setJoker(true);
        bet.setPoints(6);

        // a knockout match decided by penalty shoot-out, the bet is a draw with the other team as penalty winner
        Match knockout = matches.get(1);
        knockout.setGroup(Group.ROUND_OF_SIXTEEN);
        knockout.setGoalsTeamOne(1);
        knockout.setGoalsTeamTwo(1);
        knockout.setPenaltyWinnerOne(true);
        Bet penaltyBet = new Bet();
        penaltyBet.setUserName("admin");
        penaltyBet.setMatch(knockout);
        penaltyBet.setGoalsTeamOne(0);
        penaltyBet.setGoalsTeamTwo(0);
        penaltyBet.setPenaltyWinnerOne(false);
        penaltyBet.setPoints(0);

        when(matchService.findAllByOrderByKickOffDateAsc()).thenReturn(matches);
        when(bettingService.findAllByUsername("admin")).thenReturn(List.of(bet, penaltyBet));

        MvcTestResult result = mvc.get().uri("/matches/pdf").exchange();
        assertThat(result).hasStatusOk().hasContentTypeCompatibleWith("application/pdf");
        byte[] pdf = result.getResponse().getContentAsByteArray();
        FileUtils.writeByteArrayToFile(new File("target/my_bets.pdf"), pdf);

        PdfReader reader = new PdfReader(pdf);
        try {
            int pages = reader.getNumberOfPages();
            assertThat(pages).isGreaterThan(1);
            assertThat(reader.getPageSize(1).getWidth()).isEqualTo(PageSize.A4.getWidth());

            PdfTextExtractor extractor = new PdfTextExtractor(reader);
            String firstPage = extractor.getTextFromPage(1);
            assertThat(firstPage).contains("FredBet My bets", "admin", "total points: 6", "Team1A", "2:1 (Joker)", "+6", "1:1 (Team2A)", "0:0 (Team2B)");
            // all matches are exported, also those without a bet, with the header repeated on every page
            String lastPage = extractor.getTextFromPage(pages);
            assertThat(lastPage).contains("Team64B", "Match", "correct result");
        } finally {
            reader.close();
        }
    }

    private Match match(Long id, LocalDateTime kickOff, String teamOne, String teamTwo) {
        Match match = new Match();
        ReflectionTestUtils.setField(match, "id", id);
        match.setTeamOne(team(id * 10, teamOne));
        match.setTeamTwo(team(id * 10 + 1, teamTwo));
        match.setKickOffDate(kickOff);
        match.setGroup(Group.GROUP_A);
        return match;
    }

    private Team team(Long id, String name) {
        Team team = new Team(name);
        ReflectionTestUtils.setField(team, "id", id);
        return team;
    }

}

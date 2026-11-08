package de.fred4jupiter.fredbet.betting;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import de.fred4jupiter.fredbet.betting.repository.BetRepository;
import de.fred4jupiter.fredbet.common.TransactionalIntegrationTest;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.builder.MatchBuilder;
import de.fred4jupiter.fredbet.domain.entity.Bet;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.team.TeamService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@TransactionalIntegrationTest
public class BetsPdfExportServiceIT {

    @Autowired
    private BetsPdfExportService betsPdfExportService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private ExtraBettingService extraBettingService;

    @Test
    public void exportBetsOfAllUsersWithExtraBetsAndWithoutAdmin() throws IOException {
        Match match = MatchBuilder.create(teamService).withGroup(Group.GROUP_A).withTeams("Pdfland", "Exportia")
            .withGoals(2, 1).withKickOffDate(14, 6, 21).build();
        matchService.save(match);

        saveBet(match, "pdfuser_anna", 2, 1, true);
        saveBet(match, "pdfuser_bert", 0, 3, false);
        saveBet(match, "admin", 1, 0, false);
        extraBettingService.saveExtraBet(match.getTeamOne(), match.getTeamTwo(), null, "pdfuser_anna");
        // has an extra bet, but no match bets
        extraBettingService.saveExtraBet(match.getTeamTwo(), match.getTeamOne(), null, "pdfuser_carl");

        byte[] pdf = betsPdfExportService.exportBetsOfAllUsers(Locale.ENGLISH);
        assertThat(pdf).isNotNull();
        FileUtils.writeByteArrayToFile(new File("target/result_all_bets.pdf"), pdf);

        String text = extractText(pdf);
        assertThat(text).contains("FredBet All bets", "Bonus bets", "Pdfland – Exportia", "2:1",
            "pdfuser_anna", "pdfuser_bert", "pdfuser_carl", "Joker");
        assertThat(text).doesNotContain("admin");
    }

    private void saveBet(Match match, String username, int goalsOne, int goalsTwo, boolean joker) {
        Bet bet = new Bet();
        bet.setMatch(match);
        bet.setUserName(username);
        bet.setGoalsTeamOne(goalsOne);
        bet.setGoalsTeamTwo(goalsTwo);
        bet.setJoker(joker);
        betRepository.save(bet);
    }

    private String extractText(byte[] pdf) throws IOException {
        PdfReader reader = new PdfReader(pdf);
        try {
            PdfTextExtractor extractor = new PdfTextExtractor(reader);
            StringBuilder text = new StringBuilder();
            for (int page = 1; page <= reader.getNumberOfPages(); page++) {
                text.append(extractor.getTextFromPage(page)).append('\n');
            }
            return text.toString();
        } finally {
            reader.close();
        }
    }
}

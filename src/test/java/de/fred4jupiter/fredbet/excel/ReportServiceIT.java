package de.fred4jupiter.fredbet.excel;

import de.fred4jupiter.fredbet.betting.repository.BetRepository;
import de.fred4jupiter.fredbet.betting.repository.ExtraBetRepository;
import de.fred4jupiter.fredbet.common.TransactionalIntegrationTest;
import de.fred4jupiter.fredbet.data.DataService;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.builder.MatchBuilder;
import de.fred4jupiter.fredbet.domain.entity.Bet;
import de.fred4jupiter.fredbet.domain.entity.ExtraBet;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.team.TeamService;
import de.fred4jupiter.fredbet.util.DateUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@TransactionalIntegrationTest
public class ReportServiceIT {

    private static final LocalDateTime KICK_OFF = LocalDateTime.of(2026, 6, 14, 18, 0);

    @Autowired
    private ReportService reportService;

    @Autowired
    private DataService dataService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private ExtraBetRepository extraBetRepository;

    @Autowired
    private FredbetProperties fredbetProperties;

    private Match finishedMatch;

    @BeforeEach
    public void setup() {
        dataService.deleteAllBetsMatchesAndTeams();

        finishedMatch = matchService.save(MatchBuilder.create(teamService).withTeams(Country.GERMANY, Country.FRANCE)
            .withGroup(Group.GROUP_A).withKickOffDate(KICK_OFF).withGoals(2, 1).build());
        Match openMatch = matchService.save(MatchBuilder.create(teamService).withTeams(Country.SPAIN, Country.ITALY)
            .withGroup(Group.GROUP_A).withKickOffDate(KICK_OFF.plusDays(1)).build());

        betRepository.save(bet("anna", finishedMatch, 2, 1, true, 6));
        betRepository.save(bet("anna", openMatch, 1, 0, false, 0));
        betRepository.save(bet("bert", finishedMatch, 0, 0, false, 0));
        // the bets of the admin are never exported
        betRepository.save(bet(fredbetProperties.adminUsername(), finishedMatch, 2, 1, false, 3));

        extraBetRepository.save(extraBet("anna", 10));
        extraBetRepository.save(extraBet(fredbetProperties.adminUsername(), 10));

        // the ranking is calculated with plain SQL, so the entities have to be written to the database first
        betRepository.flush();
        extraBetRepository.flush();
    }

    @Test
    public void exportBetsOrderedByPointsWithoutAdmin() throws IOException {
        List<List<String>> rows = readSheet(reportService.exportBetsToExcel(Locale.ENGLISH));

        String kickOff = DateUtils.formatByLocale(KICK_OFF, Locale.ENGLISH);
        assertThat(rows).containsExactly(
            List.of("User Name", "Team One", "Team Two", "Date", "Result Team 1", "Result Team 2", "Bet Team 1", "Bet Team 2", "Joker", "Points"),
            List.of("anna", "Germany", "France", kickOff, "2", "1", "2", "1", "yes", "6"),
            // the bet of a match without result is exported without goals
            List.of("anna", "Spain", "Italy", DateUtils.formatByLocale(KICK_OFF.plusDays(1), Locale.ENGLISH), "", "", "", "", "no", "0"),
            List.of("bert", "Germany", "France", kickOff, "2", "1", "0", "0", "no", "0"));
    }

    @Test
    public void exportBetsWithTranslatedHeaderAndTeamNames() throws IOException {
        List<List<String>> rows = readSheet(reportService.exportBetsToExcel(Locale.GERMAN));

        assertThat(rows.get(0)).startsWith("Benutzername");
        assertThat(rows.get(1)).startsWith("anna", "Deutschland", "Frankreich");
        assertThat(rows.get(1)).contains("ja");
    }

    @Test
    public void exportExtraBetsWithoutAdmin() throws IOException {
        List<List<String>> rows = readSheet(reportService.exportExtraBetsToExcel(Locale.ENGLISH));

        assertThat(rows).containsExactly(
            List.of("Username", "champion", "Points champion", "vice champion", "Points vice champion", "3rd Winner", "Points 3rd Winner"),
            // no third place was picked, so the cell stays empty
            List.of("anna", "Germany", "10", "France", "0", "", "0"));
    }

    @Test
    public void exportNumberOfPointsInBetsWithoutAdmin() throws IOException {
        List<List<String>> rows = readSheet(reportService.exportNumberOfPointsInBets(Locale.ENGLISH));

        assertThat(rows).containsExactly(
            List.of("User Name", "Points", "Reached points count"),
            List.of("anna", "6", "1"),
            List.of("anna", "0", "1"),
            List.of("bert", "0", "1"));
    }

    @Test
    public void exportRankingWithBetAndExtraBetPoints() throws IOException {
        List<List<String>> rows = readSheet(reportService.exportRankingToExcel(Locale.ENGLISH));

        assertThat(rows).containsExactly(
            List.of("Name", "Points"),
            List.of("anna", "16"),
            List.of("bert", "0"));
    }

    @Test
    public void reportPointsFrequencyGroupedByPoints() {
        MultiValuedMap<Integer, PointCountResult> frequency = reportService.reportPointsFrequency();

        assertThat(frequency.keySet()).containsExactlyInAnyOrder(6, 0);
        assertThat(frequency.get(6)).extracting(PointCountResult::username).containsExactly("anna");
        assertThat(frequency.get(0)).extracting(PointCountResult::username).containsExactlyInAnyOrder("anna", "bert");
        assertThat(frequency.values()).extracting(PointCountResult::username).doesNotContain(fredbetProperties.adminUsername());
    }

    private List<List<String>> readSheet(byte[] excel) throws IOException {
        assertThat(excel).isNotEmpty();
        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excel))) {
            assertThat(workbook.getNumberOfSheets()).isEqualTo(1);
            Sheet sheet = workbook.getSheetAt(0);
            List<List<String>> rows = new ArrayList<>();
            for (Row row : sheet) {
                List<String> cells = new ArrayList<>();
                for (int i = 0; i < row.getLastCellNum(); i++) {
                    cells.add(row.getCell(i).getStringCellValue());
                }
                rows.add(cells);
            }
            return rows;
        }
    }

    private Bet bet(String userName, Match match, int goalsTeamOne, int goalsTeamTwo, boolean joker, int points) {
        Bet bet = new Bet();
        bet.setUserName(userName);
        bet.setMatch(match);
        bet.setGoalsTeamOne(goalsTeamOne);
        bet.setGoalsTeamTwo(goalsTeamTwo);
        bet.setJoker(joker);
        bet.setPoints(points);
        return bet;
    }

    private ExtraBet extraBet(String userName, int pointsOne) {
        ExtraBet extraBet = new ExtraBet();
        extraBet.setUserName(userName);
        extraBet.setFinalWinner(finishedMatch.getTeamOne());
        extraBet.setSemiFinalWinner(finishedMatch.getTeamTwo());
        extraBet.setPointsOne(pointsOne);
        return extraBet;
    }
}

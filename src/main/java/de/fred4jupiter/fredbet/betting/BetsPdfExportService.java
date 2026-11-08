package de.fred4jupiter.fredbet.betting;

import com.lowagie.text.Element;
import de.fred4jupiter.fredbet.domain.entity.Bet;
import de.fred4jupiter.fredbet.domain.entity.ExtraBet;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.pdf.PdfExportService;
import de.fred4jupiter.fredbet.pdf.PdfSection;
import de.fred4jupiter.fredbet.pdf.PdfTableData;
import de.fred4jupiter.fredbet.pdf.PdfTableDataBuilder;
import de.fred4jupiter.fredbet.points.BetOutcome;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Exports all matches with the bets of a user or of all users as PDF (DIN A4).
 */
@Service
@Transactional(readOnly = true)
public class BetsPdfExportService {

    // light variants of the colors of the points badges (custom.css), so the PDF is still readable when printed
    private static final Map<BetOutcome, Color> OUTCOME_COLORS = new EnumMap<>(Map.of(
        BetOutcome.CORRECT_RESULT, new Color(209, 231, 221),
        BetOutcome.SAME_GOAL_DIFFERENCE, new Color(210, 244, 234),
        BetOutcome.CORRECT_WINNER, new Color(255, 243, 205),
        BetOutcome.CORRECT_NUMBER_OF_GOALS_ONE_TEAM, new Color(255, 229, 208)));

    private static final String NO_VALUE = "–";

    private final MatchService matchService;

    private final BettingService bettingService;

    private final PdfExportService pdfExportService;

    private final MessageSourceUtil messageSourceUtil;

    private final ExtraBettingService extraBettingService;

    private final FredbetProperties fredbetProperties;

    BetsPdfExportService(MatchService matchService, BettingService bettingService, PdfExportService pdfExportService,
                         MessageSourceUtil messageSourceUtil, ExtraBettingService extraBettingService,
                         FredbetProperties fredbetProperties) {
        this.matchService = matchService;
        this.bettingService = bettingService;
        this.pdfExportService = pdfExportService;
        this.messageSourceUtil = messageSourceUtil;
        this.extraBettingService = extraBettingService;
        this.fredbetProperties = fredbetProperties;
    }

    public byte[] exportBetsOfUser(String username, Locale locale) {
        final List<Match> matches = matchService.findAllByOrderByKickOffDateAsc();
        final Map<Long, Bet> betsByMatchId = bettingService.findAllByUsername(username).stream()
            .filter(bet -> bet.getMatch() != null)
            .collect(Collectors.toMap(bet -> bet.getMatch().getId(), Function.identity(), (first, second) -> first));

        final int totalPoints = betsByMatchId.values().stream().map(Bet::getPoints).filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
        final String subtitle = "%s · %s: %d".formatted(username, message("pdf.export.totalPoints", locale), totalPoints);

        PdfTableDataBuilder builder = PdfTableDataBuilder.create()
            .withTitle("FredBet " + message("pdf.export.myBets.title", locale))
            .withSubtitle(subtitle)
            .withLocale(locale)
            .withHeaderColumn(message("pdf.export.date", locale), Element.ALIGN_LEFT)
            .withHeaderColumn(message("pdf.export.group", locale), Element.ALIGN_LEFT)
            .withHeaderColumn(message("pdf.export.match", locale), Element.ALIGN_LEFT)
            .withHeaderColumn(message("pdf.export.result", locale))
            .withHeaderColumn(message("pdf.export.bet", locale))
            .withHeaderColumn(message("pdf.export.points", locale))
            .withColumnWidths(new float[]{2.6f, 2.0f, 4.0f, 2.2f, 2.4f, 1.2f});
        OUTCOME_COLORS.forEach((outcome, color) -> builder.withLegendEntry(color, message(outcome.getMsgKey(), locale)));

        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEE dd.MM. HH:mm", locale);
        final String joker = message("excel.export.joker", locale);

        return pdfExportService.createPdfFileFrom(builder.build(), matches, (rowContentAdder, match) -> {
            final Bet bet = betsByMatchId.get(match.getId());

            rowContentAdder.addCellContent(match.getKickOffDate() != null ? match.getKickOffDate().format(dateFormatter) : "");
            rowContentAdder.addCellContent(match.getGroup() != null ? message(match.getGroup().getTitleMsgKey(), locale) : "");
            rowContentAdder.addCellContent(match.getTeamOne().getNameTranslated(messageSourceUtil, locale) + " – "
                + match.getTeamTwo().getNameTranslated(messageSourceUtil, locale));
            rowContentAdder.addCellContent(resultText(match, locale));
            rowContentAdder.addCellContent(betText(match, bet, joker, locale));
            addPoints(rowContentAdder, match, bet);
        });
    }

    /**
     * Exports an overview of the extra bets of all users, followed by all matches, each with the bets of all users. Users
     * without a bet for a match are listed too. The admin user is left out, like in the Excel exports.
     */
    public byte[] exportBetsOfAllUsers(Locale locale) {
        final List<Match> matches = matchService.findAllByOrderByKickOffDateAsc();
        final List<Bet> bets = bettingService.findAll().stream()
            .filter(bet -> bet.getMatch() != null && isNotAdmin(bet.getUserName()))
            .toList();
        final Map<String, ExtraBet> extraBetsByUser = extraBettingService.findAllExtraBets().stream()
            .filter(extraBet -> isNotAdmin(extraBet.getUserName()))
            .collect(Collectors.toMap(ExtraBet::getUserName, Function.identity(), (first, second) -> first));

        final SortedSet<String> usernames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        bets.forEach(bet -> usernames.add(bet.getUserName()));
        usernames.addAll(extraBetsByUser.keySet());
        final List<String> sortedUsernames = List.copyOf(usernames);

        final List<PdfSection> sections = new ArrayList<>();
        sections.add(createExtraBetsSection(sortedUsernames, extraBetsByUser, locale));

        final Map<Long, Map<String, Bet>> betsByMatchAndUser = bets.stream().collect(Collectors.groupingBy(
            bet -> bet.getMatch().getId(), Collectors.toMap(Bet::getUserName, Function.identity(), (first, second) -> first)));
        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEE dd.MM.yyyy HH:mm", locale);
        for (Match match : matches) {
            final Map<String, Bet> betsByUser = betsByMatchAndUser.getOrDefault(match.getId(), Map.of());
            sections.add(createMatchSection(match, sortedUsernames, betsByUser, dateFormatter, locale));
        }

        final String subtitle = "%s: %d · %s: %d".formatted(message("pdf.export.users", locale), sortedUsernames.size(),
            message("pdf.export.matches", locale), matches.size());
        final List<PdfTableData.LegendEntry> legend = OUTCOME_COLORS.entrySet().stream()
            .map(entry -> new PdfTableData.LegendEntry(entry.getValue(), message(entry.getKey().getMsgKey(), locale)))
            .toList();

        return pdfExportService.createPdfWithSections("FredBet " + message("pdf.export.allBets.title", locale), subtitle,
            locale, sections, legend);
    }

    private PdfSection createExtraBetsSection(List<String> usernames, Map<String, ExtraBet> extraBetsByUser, Locale locale) {
        final String points = message("pdf.export.points", locale);
        PdfTableData tableData = PdfTableDataBuilder.create()
            .withTitle(message("pdf.export.extraBets", locale))
            .withLocale(locale)
            .withHeaderColumn(message("pdf.export.username", locale), Element.ALIGN_LEFT)
            .withHeaderColumn(message("excel.export.extrabet.finalWinner", locale), Element.ALIGN_LEFT)
            .withHeaderColumn(points)
            .withHeaderColumn(message("excel.export.extrabet.semiFinalWinner", locale), Element.ALIGN_LEFT)
            .withHeaderColumn(points)
            .withHeaderColumn(message("excel.export.extrabet.thirdFinalWinner", locale), Element.ALIGN_LEFT)
            .withHeaderColumn(points)
            .withHeaderColumn(message("pdf.export.totalPoints", locale))
            .withColumnWidths(new float[]{2.6f, 2.4f, 1.7f, 2.4f, 1.7f, 2.4f, 1.7f, 2.0f})
            .build();

        return pdfExportService.createSection(tableData, usernames, (rowContentAdder, username) -> {
            final ExtraBet extraBet = extraBetsByUser.get(username);
            rowContentAdder.addCellContent(username);
            if (extraBet == null) {
                for (int i = 0; i < 7; i++) {
                    rowContentAdder.addCellContent(NO_VALUE);
                }
                return;
            }

            addExtraBet(rowContentAdder, extraBet.getFinalWinner(), extraBet.getPointsOne(), locale);
            addExtraBet(rowContentAdder, extraBet.getSemiFinalWinner(), extraBet.getPointsTwo(), locale);
            addExtraBet(rowContentAdder, extraBet.getThirdFinalWinner(), extraBet.getPointsThree(), locale);
            final int total = Stream.of(extraBet.getPointsOne(), extraBet.getPointsTwo(), extraBet.getPointsThree())
                .filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
            rowContentAdder.addCellContent(String.valueOf(total));
        });
    }

    private void addExtraBet(PdfExportService.RowContentAdder rowContentAdder, Team team, Integer points, Locale locale) {
        rowContentAdder.addCellContent(team != null ? team.getNameTranslated(messageSourceUtil, locale) : NO_VALUE);
        if (points != null && points > 0) {
            rowContentAdder.addCellContent("+" + points, OUTCOME_COLORS.get(BetOutcome.CORRECT_RESULT));
        } else {
            rowContentAdder.addCellContent(points != null ? String.valueOf(points) : NO_VALUE);
        }
    }

    private PdfSection createMatchSection(Match match, List<String> usernames, Map<String, Bet> betsByUser,
                                          DateTimeFormatter dateFormatter, Locale locale) {
        final String teams = match.getTeamOne().getNameTranslated(messageSourceUtil, locale) + " – "
            + match.getTeamTwo().getNameTranslated(messageSourceUtil, locale);
        final String result = resultText(match, locale);
        final String heading = NO_VALUE.equals(result) ? teams : teams + "   " + result;

        final List<String> details = new ArrayList<>();
        if (match.getKickOffDate() != null) {
            details.add(match.getKickOffDate().format(dateFormatter));
        }
        if (match.getGroup() != null) {
            details.add(message(match.getGroup().getTitleMsgKey(), locale));
        }

        PdfTableData tableData = PdfTableDataBuilder.create()
            .withTitle(heading)
            .withSubtitle(String.join(" · ", details))
            .withLocale(locale)
            .withHeaderColumn(message("pdf.export.username", locale), Element.ALIGN_LEFT)
            .withHeaderColumn(message("pdf.export.bet", locale))
            .withHeaderColumn(message("pdf.export.points", locale))
            .withColumnWidths(new float[]{5.0f, 4.0f, 1.6f})
            .build();

        final String joker = message("excel.export.joker", locale);
        return pdfExportService.createSection(tableData, usernames, (rowContentAdder, username) -> {
            final Bet bet = betsByUser.get(username);
            rowContentAdder.addCellContent(username);
            rowContentAdder.addCellContent(betText(match, bet, joker, locale));
            addPoints(rowContentAdder, match, bet);
        });
    }

    private boolean isNotAdmin(String username) {
        return !fredbetProperties.adminUsername().equals(username);
    }

    private String resultText(Match match, Locale locale) {
        if (!match.hasResultSet()) {
            return NO_VALUE;
        }
        final String result = match.getGoalsTeamOne() + ":" + match.getGoalsTeamTwo();
        if (match.isKnockoutMatch() && match.isUndecidedResult()) {
            return result + " (" + penaltyWinner(match, match.isPenaltyWinnerOne(), locale) + ")";
        }
        return result;
    }

    private String betText(Match match, Bet bet, String joker, Locale locale) {
        if (bet == null || bet.getGoalsTeamOne() == null || bet.getGoalsTeamTwo() == null) {
            return NO_VALUE;
        }

        final List<String> additions = new ArrayList<>();
        // a draw in a knockout match is decided by the penalty shoot-out, so the bet contains the penalty winner
        if (match.isKnockoutMatch() && bet.isUndecidedBetting()) {
            additions.add(penaltyWinner(match, bet.isPenaltyWinnerOne(), locale));
        }
        if (bet.isJoker()) {
            additions.add(joker);
        }

        final String result = bet.getGoalsTeamOne() + ":" + bet.getGoalsTeamTwo();
        return additions.isEmpty() ? result : result + " (" + String.join(", ", additions) + ")";
    }

    private String penaltyWinner(Match match, boolean penaltyWinnerOne, Locale locale) {
        final Team winner = penaltyWinnerOne ? match.getTeamOne() : match.getTeamTwo();
        return winner.getNameTranslated(messageSourceUtil, locale);
    }

    private void addPoints(PdfExportService.RowContentAdder rowContentAdder, Match match, Bet bet) {
        if (!match.hasMatchFinished()) {
            rowContentAdder.addCellContent(NO_VALUE);
            return;
        }

        final int points = bet != null && bet.getPoints() != null ? bet.getPoints() : 0;
        if (points == 0) {
            rowContentAdder.addCellContent("0");
            return;
        }
        rowContentAdder.addCellContent("+" + points, OUTCOME_COLORS.get(BetOutcome.of(match, bet)));
    }

    private String message(String key, Locale locale) {
        return messageSourceUtil.getMessageFor(key, locale);
    }
}

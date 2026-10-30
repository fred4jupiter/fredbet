package de.fred4jupiter.fredbet.betting;

import com.lowagie.text.Element;
import de.fred4jupiter.fredbet.domain.entity.Bet;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.pdf.PdfExportService;
import de.fred4jupiter.fredbet.pdf.PdfTableDataBuilder;
import de.fred4jupiter.fredbet.points.BetOutcome;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Exports all matches with the bets of a user as PDF (DIN A4).
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

    BetsPdfExportService(MatchService matchService, BettingService bettingService, PdfExportService pdfExportService,
                         MessageSourceUtil messageSourceUtil) {
        this.matchService = matchService;
        this.bettingService = bettingService;
        this.pdfExportService = pdfExportService;
        this.messageSourceUtil = messageSourceUtil;
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
            .withColumnWidths(new float[]{2.6f, 2.2f, 4.8f, 1.3f, 1.8f, 1.2f});
        OUTCOME_COLORS.forEach((outcome, color) -> builder.withLegendEntry(color, message(outcome.getMsgKey(), locale)));

        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEE dd.MM. HH:mm", locale);
        final String joker = message("excel.export.joker", locale);

        return pdfExportService.createPdfFileFrom(builder.build(), matches, (rowContentAdder, match) -> {
            final Bet bet = betsByMatchId.get(match.getId());

            rowContentAdder.addCellContent(match.getKickOffDate() != null ? match.getKickOffDate().format(dateFormatter) : "");
            rowContentAdder.addCellContent(match.getGroup() != null ? message(match.getGroup().getTitleMsgKey(), locale) : "");
            rowContentAdder.addCellContent(match.getTeamOne().getNameTranslated(messageSourceUtil, locale) + " – "
                + match.getTeamTwo().getNameTranslated(messageSourceUtil, locale));
            rowContentAdder.addCellContent(match.hasResultSet() ? match.getGoalsTeamOne() + ":" + match.getGoalsTeamTwo() : NO_VALUE);
            rowContentAdder.addCellContent(betText(bet, joker));
            addPoints(rowContentAdder, match, bet);
        });
    }

    private String betText(Bet bet, String joker) {
        if (bet == null || bet.getGoalsTeamOne() == null || bet.getGoalsTeamTwo() == null) {
            return NO_VALUE;
        }
        final String result = bet.getGoalsTeamOne() + ":" + bet.getGoalsTeamTwo();
        return bet.isJoker() ? result + " (" + joker + ")" : result;
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

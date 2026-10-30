package de.fred4jupiter.fredbet.ranking;

import de.fred4jupiter.fredbet.domain.entity.Bet;
import de.fred4jupiter.fredbet.domain.RankingSelection;
import de.fred4jupiter.fredbet.settings.RuntimeSettings;
import de.fred4jupiter.fredbet.domain.Visitable;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.betting.repository.BetRepository;
import de.fred4jupiter.fredbet.settings.RuntimeSettingsService;
import de.fred4jupiter.fredbet.pdf.PdfExportService;
import de.fred4jupiter.fredbet.pdf.PdfTableDataBuilder;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import com.lowagie.text.Element;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.util.*;
import java.util.stream.Stream;

@Service
@Transactional
public class RankingService {

    // same colors as on the ranking page (ranking.css)
    private static final Color SHARED_POSITION_COLOR = new Color(255, 243, 205);

    private static final Map<Integer, Color> MEDAL_COLORS = Map.of(
        1, new Color(245, 197, 24),
        2, new Color(192, 196, 200),
        3, new Color(211, 154, 99));

    private final BetRepository betRepository;

    private final ChildRelationFetcher childRelationFetcher;

    private final PdfExportService pdfExportService;

    private final MessageSourceUtil messageSourceUtil;

    private final RuntimeSettingsService runtimeSettingsService;

    private final SameRankingCollector sameRankingCollector;

    private final FredbetProperties fredbetProperties;

    RankingService(BetRepository betRepository, ChildRelationFetcher childRelationFetcher, PdfExportService pdfExportService,
                          MessageSourceUtil messageSourceUtil, RuntimeSettingsService runtimeSettingsService,
                          SameRankingCollector sameRankingCollector, FredbetProperties fredbetProperties) {
        this.betRepository = betRepository;
        this.childRelationFetcher = childRelationFetcher;
        this.pdfExportService = pdfExportService;
        this.messageSourceUtil = messageSourceUtil;
        this.runtimeSettingsService = runtimeSettingsService;
        this.sameRankingCollector = sameRankingCollector;
        this.fredbetProperties = fredbetProperties;
    }

    public List<UsernamePoints> calculateCurrentRanking(RankingSelection rankingSelection) {
        final List<UsernamePoints> rankings = betRepository.calculateRanging();

        calculateAdditionalMetricsForRanking(rankings);

        // positions and ties are determined within the selection (e.g. only adults), not across all users
        final List<UsernamePoints> sortedRankings = filterAndSortRankings(rankingSelection, rankings);
        sameRankingCollector.markEntriesWithSameRanking(sortedRankings);
        sameRankingCollector.assignRankPositions(sortedRankings);
        return sortedRankings;
    }

    private void calculateAdditionalMetricsForRanking(List<UsernamePoints> rankings) {
        final List<Bet> allBetsWithMatches = betRepository.findAllBetsWithMatches();
        final List<String> topTipperUsernames = betRepository.queryPointsPerUserForToday(fredbetProperties.adminUsername());

        final CorrectResultVisitor correctResultVisitor = new CorrectResultVisitor();
        final GoalDifferenceVisitor goalDifferenceVisitor = new GoalDifferenceVisitor();
        for (Visitable bet : allBetsWithMatches) {
            bet.accept(correctResultVisitor);
            bet.accept(goalDifferenceVisitor);
        }

        rankings.stream().filter(Objects::nonNull).forEach(usernamePoints -> {
            usernamePoints.setCorrectResultCount(correctResultVisitor.getTotalCorrectResultCountForUser(usernamePoints.getUserName()));
            usernamePoints.setGoalDifference(goalDifferenceVisitor.getTotalGoalDifferenceForUser(usernamePoints.getUserName()));
            if (topTipperUsernames.contains(usernamePoints.getUserName())) {
                usernamePoints.setTopTipperOfToday(true);
            }
        });
    }

    private List<UsernamePoints> filterAndSortRankings(RankingSelection rankingSelection, List<UsernamePoints> rankings) {
        Stream<UsernamePoints> usernamePointsStream = prepareUsernamePoints(rankings, rankingSelection);

        Comparator<UsernamePoints> comparator1 = Comparator.comparingInt(UsernamePoints::getTotalPoints).reversed();
        Comparator<UsernamePoints> comparator2 = Comparator.comparingInt(UsernamePoints::getCorrectResultCount).reversed();
        Comparator<UsernamePoints> comparator3 = Comparator.comparingInt(UsernamePoints::getGoalDifference);

        return usernamePointsStream.sorted(comparator1.thenComparing(comparator2).thenComparing(comparator3)).toList();
    }

    private Stream<UsernamePoints> prepareUsernamePoints(List<UsernamePoints> rankings, RankingSelection rankingSelection) {
        final Map<String, Boolean> relationMap = childRelationFetcher.fetchUserIsChildRelation();

        return switch (rankingSelection) {
            case MIXED -> rankings.stream().filter(Objects::nonNull);
            case ONLY_ADULTS ->
                rankings.stream().filter(Objects::nonNull).filter(usernamePoints -> !isChild(relationMap, usernamePoints));
            case ONLY_CHILDREN ->
                rankings.stream().filter(Objects::nonNull).filter(usernamePoints -> isChild(relationMap, usernamePoints));
        };
    }

    private Boolean isChild(Map<String, Boolean> relationMap, UsernamePoints usernamePoints) {
        Boolean isChild = relationMap.get(usernamePoints.getUserName());
        return isChild != null && isChild;
    }

    public byte[] exportBetsToPdf(Locale locale, RankingSelection rankingSelection) {
        final String title = "FredBet " + messageSourceUtil.getMessageFor("ranking.list.title", locale);
        final RuntimeSettings runtimeSettings = runtimeSettingsService.loadRuntimeSettings();
        final boolean parentChildRanking = runtimeSettings.isEnabledParentChildRanking();

        PdfTableDataBuilder builder = PdfTableDataBuilder.create()
            .withTitle(title)
            .withLocale(locale)
            .withHeaderColumn("#")
            .withHeaderColumn(messageSourceUtil.getMessageFor("pdf.export.username", locale), Element.ALIGN_LEFT);
        if (parentChildRanking) {
            builder.withSubtitle(messageSourceUtil.getMessageFor(rankingSelectionMessageKey(rankingSelection), locale));
            builder.withHeaderColumn(messageSourceUtil.getMessageFor("pdf.export.child", locale));
            builder.withColumnWidths(new float[]{1, 5, 1.5f, 2, 2, 2});
        } else {
            builder.withColumnWidths(new float[]{1, 5, 2, 2, 2});
        }
        builder.withHeaderColumn(messageSourceUtil.getMessageFor("pdf.export.totalPoints", locale))
            .withHeaderColumn(messageSourceUtil.getMessageFor("pdf.export.correctResult", locale))
            .withHeaderColumn(messageSourceUtil.getMessageFor("pdf.export.goalDifference", locale));

        final List<UsernamePoints> rankings = calculateCurrentRanking(rankingSelection);
        if (rankings.stream().anyMatch(UsernamePoints::isSameRankingPositionAsOtherUser)) {
            builder.withLegendEntry(SHARED_POSITION_COLOR, messageSourceUtil.getMessageFor("ranking.legend.sharedPosition", locale));
        }
        final Map<String, Boolean> relationMap = childRelationFetcher.fetchUserIsChildRelation();

        return pdfExportService.createPdfFileFrom(builder.build(), rankings, (rowContentAdder, row) -> {
            if (row.isSameRankingPositionAsOtherUser()) {
                rowContentAdder.setRowBackground(SHARED_POSITION_COLOR);
            }
            rowContentAdder.addCellContent(String.valueOf(row.getRankPosition()), MEDAL_COLORS.get(row.getRankPosition()));
            rowContentAdder.addCellContent(row.getUserName());
            if (parentChildRanking) {
                rowContentAdder.addCellContent(Boolean.TRUE.equals(relationMap.get(row.getUserName())) ? "X" : "");
            }
            rowContentAdder.addCellContent(String.valueOf(row.getTotalPoints()));
            rowContentAdder.addCellContent(String.valueOf(row.getCorrectResultCount()));
            rowContentAdder.addCellContent(String.valueOf(row.getGoalDifference()));
        });
    }

    private String rankingSelectionMessageKey(RankingSelection rankingSelection) {
        return switch (rankingSelection) {
            case MIXED -> "ranking.mode.mixed";
            case ONLY_ADULTS -> "ranking.mode.adults";
            case ONLY_CHILDREN -> "ranking.mode.children";
        };
    }
}

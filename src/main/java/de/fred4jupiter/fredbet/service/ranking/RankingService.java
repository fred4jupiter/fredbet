package de.fred4jupiter.fredbet.service.ranking;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.RankingSelection;
import de.fred4jupiter.fredbet.domain.RuntimeSettings;
import de.fred4jupiter.fredbet.domain.Visitable;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.repository.UsernamePoints;
import de.fred4jupiter.fredbet.service.config.RuntimeSettingsService;
import de.fred4jupiter.fredbet.service.pdf.PdfExportService;
import de.fred4jupiter.fredbet.service.pdf.PdfTableDataBuilder;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RankingService {

    private final BetRepository betRepository;

    private final ChildRelationFetcher childRelationFetcher;

    private final PdfExportService pdfExportService;

    private final MessageSourceUtil messageSourceUtil;

    private final RuntimeSettingsService runtimeSettingsService;

    public RankingService(BetRepository betRepository, ChildRelationFetcher childRelationFetcher, PdfExportService pdfExportService,
                          MessageSourceUtil messageSourceUtil, RuntimeSettingsService runtimeSettingsService) {
        this.betRepository = betRepository;
        this.childRelationFetcher = childRelationFetcher;
        this.pdfExportService = pdfExportService;
        this.messageSourceUtil = messageSourceUtil;
        this.runtimeSettingsService = runtimeSettingsService;
    }

    public List<UsernamePoints> calculateCurrentRanking(RankingSelection rankingSelection) {
        final List<UsernamePoints> rankings = betRepository.calculateRanging();

        calculateAdditionalMetricsForRanking(rankings);

        SameRankingCollector collector = new SameRankingCollector();
        collector.markEntriesWithSameRanking(rankings);

        return filterAndSortRankings(rankingSelection, rankings);
    }

    private void calculateAdditionalMetricsForRanking(List<UsernamePoints> rankings) {
        List<Bet> allBetsWithMatches = betRepository.findAllBetsWithMatches();

        final CorrectResultVisitor correctResultVisitor = new CorrectResultVisitor();
        final GoalDifferenceVisitor goalDifferenceVisitor = new GoalDifferenceVisitor();
        for (Visitable bet : allBetsWithMatches) {
            bet.accept(correctResultVisitor);
            bet.accept(goalDifferenceVisitor);
        }

        rankings.stream().filter(Objects::nonNull).forEach(usernamePoints -> {
            usernamePoints.setCorrectResultCount(correctResultVisitor.getTotalCorrectResultCountForUser(usernamePoints.getUserName()));
            usernamePoints.setGoalDifference(goalDifferenceVisitor.getTotalGoalDifferenceForUser(usernamePoints.getUserName()));
        });
    }

    private List<UsernamePoints> filterAndSortRankings(RankingSelection rankingSelection, List<UsernamePoints> rankings) {
        final Map<String, Boolean> relationMap = childRelationFetcher.fetchUserIsChildRelation();
        Stream<UsernamePoints> usernamePointsStream = rankings.stream().filter(Objects::nonNull);

        if (RankingSelection.MIXED.equals(rankingSelection)) {
            // nothing to filter
        } else if (RankingSelection.ONLY_ADULTS.equals(rankingSelection))
            usernamePointsStream = usernamePointsStream.filter(usernamePoints -> !isChild(relationMap, usernamePoints));
        else if (RankingSelection.ONLY_CHILDREN.equals(rankingSelection)) {
            usernamePointsStream = usernamePointsStream.filter(usernamePoints -> isChild(relationMap, usernamePoints));
        } else {
            throw new IllegalArgumentException("Unsupported ranking selection " + rankingSelection);
        }

        Comparator<UsernamePoints> comparator1 = Comparator.comparingInt(UsernamePoints::getTotalPoints).reversed();
        Comparator<UsernamePoints> comparator2 = Comparator.comparingInt(UsernamePoints::getCorrectResultCount).reversed();
        Comparator<UsernamePoints> comparator3 = Comparator.comparingInt(UsernamePoints::getGoalDifference);

        return usernamePointsStream.sorted(comparator1.thenComparing(comparator2).thenComparing(comparator3)).collect(Collectors.toList());
    }

    private Boolean isChild(Map<String, Boolean> relationMap, UsernamePoints usernamePoints) {
        Boolean isChild = relationMap.get(usernamePoints.getUserName());
        return isChild == null ? false : isChild;
    }

    public byte[] exportBetsToPdf(Locale locale) {
        final String title = "FredBet " + messageSourceUtil.getMessageFor("ranking.list.title", locale);
        PdfTableDataBuilder builder = PdfTableDataBuilder.create()
                .withHeaderColumn("#")
                .withHeaderColumn(messageSourceUtil.getMessageFor("pdf.export.username", locale));
        final RuntimeSettings runtimeSettings = runtimeSettingsService.loadRuntimeSettings();
        if (runtimeSettings.isEnabledParentChildRanking()) {
            builder.withHeaderColumn(messageSourceUtil.getMessageFor("pdf.export.child", locale));
            builder.withColumnWidths(new float[]{1, 3, 2, 3, 3, 3});
        } else {
            builder.withColumnWidths(new float[]{1, 3, 3, 3, 3});
        }
        builder.withHeaderColumn(messageSourceUtil.getMessageFor("pdf.export.totalPoints", locale))
                .withHeaderColumn(messageSourceUtil.getMessageFor("pdf.export.correctResult", locale))
                .withHeaderColumn(messageSourceUtil.getMessageFor("pdf.export.goalDifference", locale));

        builder.withTitle(title).withLocale(locale);

        final List<UsernamePoints> rankings = calculateCurrentRanking(RankingSelection.MIXED);
        final Map<String, Boolean> relationMap = childRelationFetcher.fetchUserIsChildRelation();

        final AtomicInteger rank = new AtomicInteger();
        return pdfExportService.createPdfFileFrom(builder, rankings, (rowContentAdder, row) -> {
            rowContentAdder.addCellContent("" + rank.incrementAndGet());
            rowContentAdder.addCellContent(row.getUserName());
            if (runtimeSettings.isEnabledParentChildRanking()) {
                Boolean isChild = relationMap.get(row.getUserName());
                rowContentAdder.addCellContent(isChild ? "X" : "");
            }
            rowContentAdder.addCellContent("" + row.getTotalPoints());
            rowContentAdder.addCellContent("" + row.getCorrectResultCount());
            rowContentAdder.addCellContent("" + row.getGoalDifference());
        });
    }
}

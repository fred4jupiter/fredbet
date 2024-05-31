package de.fred4jupiter.fredbet.service.ranking;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.RankingSelection;
import de.fred4jupiter.fredbet.domain.RuntimeSettings;
import de.fred4jupiter.fredbet.domain.Visitable;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.repository.UsernamePoints;
import de.fred4jupiter.fredbet.service.config.RuntimeSettingsService;
import de.fred4jupiter.fredbet.service.pdf.PdfExportService;
import de.fred4jupiter.fredbet.service.pdf.PdfTableDataBuilder;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Service
public class RankingService {

    private final BetRepository betRepository;

    private final ChildRelationFetcher childRelationFetcher;

    private final PdfExportService pdfExportService;

    private final MessageSourceUtil messageSourceUtil;

    private final RuntimeSettingsService runtimeSettingsService;

    private final SameRankingCollector sameRankingCollector;

    private final FredbetProperties fredbetProperties;

    public RankingService(BetRepository betRepository, ChildRelationFetcher childRelationFetcher, PdfExportService pdfExportService,
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

        sameRankingCollector.markEntriesWithSameRanking(rankings);

        return filterAndSortRankings(rankingSelection, rankings);
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

        final List<UsernamePoints> rankings = calculateCurrentRanking(rankingSelection);
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

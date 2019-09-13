package de.fred4jupiter.fredbet.service.ranking;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.RankingSelection;
import de.fred4jupiter.fredbet.domain.Visitable;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.repository.UsernamePoints;
import de.fred4jupiter.fredbet.service.pdf.PdfExportService;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RankingService {

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private ChildRelationFetcher childRelationFetcher;

    @Autowired
    private PdfExportService pdfExportService;

    @Autowired
    private MessageSourceUtil messageSourceUtil;

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
        List<UsernamePoints> rankings = calculateCurrentRanking(RankingSelection.MIXED);

        final List<String> headerColumns = new ArrayList<>();
        headerColumns.add("#");
        headerColumns.add(messageSourceUtil.getMessageFor("pdf.export.username", locale));
        headerColumns.add(messageSourceUtil.getMessageFor("pdf.export.correctResult", locale));
        headerColumns.add(messageSourceUtil.getMessageFor("pdf.export.goalDifference", locale));
        headerColumns.add(messageSourceUtil.getMessageFor("pdf.export.totalPoints", locale));

        final AtomicInteger rank = new AtomicInteger();
        return pdfExportService.createPdfFileFrom(title, headerColumns, rankings, (rowContentAdder, row) -> {
            rowContentAdder.addCellContent("" + rank.incrementAndGet());
            rowContentAdder.addCellContent(row.getUserName());
            rowContentAdder.addCellContent("" + row.getCorrectResultCount());
            rowContentAdder.addCellContent("" + row.getGoalDifference());
            rowContentAdder.addCellContent("" + row.getTotalPoints());
        });
    }
}

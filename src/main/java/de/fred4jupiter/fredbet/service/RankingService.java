package de.fred4jupiter.fredbet.service;

import de.fred4jupiter.fredbet.domain.RankingSelection;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.repository.UsernamePoints;
import de.fred4jupiter.fredbet.service.goaldiff.GoalsDifferenceCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RankingService {

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private ChildRelationFetcher childRelationFetcher;

    @Autowired
    private GoalsDifferenceCalculationService goalsDifferenceCalculationService;

    public List<UsernamePoints> calculateCurrentRanking(RankingSelection rankingSelection) {
        final List<UsernamePoints> rankings = betRepository.calculateRanging();

        final Map<String, Integer> goalDifferenceForUsers = goalsDifferenceCalculationService.calculateGoalDifferenceForUsers();

        rankings.forEach(usernamePoints -> {
            Integer goalDiference = goalDifferenceForUsers.get(usernamePoints.getUserName());
            usernamePoints.setGoalDifference(goalDiference);
        });

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

        Comparator<UsernamePoints> comparator = Comparator.comparing(UsernamePoints::getTotalPoints).reversed().thenComparing(UsernamePoints::getGoalDifference);
        return usernamePointsStream.sorted(comparator).collect(Collectors.toList());
    }

    private Boolean isChild(Map<String, Boolean> relationMap, UsernamePoints usernamePoints) {
        Boolean isChild = relationMap.get(usernamePoints.getUserName());
        return isChild == null ? false : isChild;
    }
}

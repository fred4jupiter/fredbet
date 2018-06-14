package de.fred4jupiter.fredbet.service;

import de.fred4jupiter.fredbet.domain.RankingSelection;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.repository.UsernamePoints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RankingService {

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private ChildRelationFetcher childRelationFetcher;

    public List<UsernamePoints> calculateCurrentRanking(RankingSelection rankingSelection) {
        if (RankingSelection.MIXED.equals(rankingSelection)) {
            return betRepository.calculateRanging();
        }

        Map<String, Boolean> relationMap = childRelationFetcher.fetchUserIsChildRelation();

        final List<UsernamePoints> ranking = betRepository.calculateRanging();

        if (RankingSelection.ONLY_ADULTS.equals(rankingSelection)) {
            return ranking.stream().filter(Objects::nonNull).filter(usernamePoints -> !isChild(relationMap, usernamePoints)).collect(Collectors.toList());
        } else if (RankingSelection.ONLY_CHILDREN.equals(rankingSelection)) {
            return ranking.stream().filter(Objects::nonNull).filter(usernamePoints -> isChild(relationMap, usernamePoints)).collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("Unsupported ranking selection " + rankingSelection);
        }
    }

    private Boolean isChild(Map<String, Boolean> relationMap, UsernamePoints usernamePoints) {
        Boolean isChild = relationMap.get(usernamePoints.getUserName());
        return isChild == null ? false : isChild;
    }
}

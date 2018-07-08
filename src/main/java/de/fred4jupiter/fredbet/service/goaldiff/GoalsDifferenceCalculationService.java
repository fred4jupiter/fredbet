package de.fred4jupiter.fredbet.service.goaldiff;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.repository.BetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoalsDifferenceCalculationService {

    @Autowired
    private BetRepository betRepository;

    public Map<String, Integer> calculateGoalDifferenceForUsers() {
        List<Bet> allBetsWithMatches = betRepository.findAllBetsWithMatches();
        if (allBetsWithMatches.isEmpty()) {
            return Collections.emptyMap();
        }

        final Map<String, Integer> goalDifferenceMap = new HashMap<>();

        allBetsWithMatches.forEach(bet -> {
            Integer differenceTeamOne = calculateGoalDifferenceFor(bet.getGoalsTeamOne(), bet.getMatch().getGoalsTeamOne());
            Integer differenceTeamTwo = calculateGoalDifferenceFor(bet.getGoalsTeamTwo(), bet.getMatch().getGoalsTeamTwo());
            goalDifferenceMap.put(bet.getUserName(), differenceTeamOne + differenceTeamTwo);
        });
        return goalDifferenceMap;
    }

    private Integer calculateGoalDifferenceFor(Integer valueOne, Integer valueTwo) {
        if (valueOne == null || valueTwo == null) {
            throw new IllegalStateException("One of the given value is not which is not allowed! valueOne=" + valueOne + ", valueTwo=" + valueTwo);
        }
        return Math.abs(valueOne - valueTwo);
    }
}

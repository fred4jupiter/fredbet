package de.fred4jupiter.fredbet.service.ranking;

import de.fred4jupiter.fredbet.domain.Bet;

import java.util.HashMap;
import java.util.Map;

class GoalDifferenceVisitor implements Visitor {

    private final Map<String, Integer> goalDifferenceMap = new HashMap<>();

    @Override
    public void visit(Bet bet) {
        Integer differenceTeamOne = calculateGoalDifferenceFor(bet.getGoalsTeamOne(), bet.getMatch().getGoalsTeamOne());
        Integer differenceTeamTwo = calculateGoalDifferenceFor(bet.getGoalsTeamTwo(), bet.getMatch().getGoalsTeamTwo());
        Integer goalDifference = differenceTeamOne + differenceTeamTwo;
        addForUser(bet.getUserName(), goalDifference);
    }

    private void addForUser(String username, Integer value) {
        Integer currentValue = goalDifferenceMap.get(username);
        if (currentValue == null) {
            currentValue = 0;
        }

        currentValue = currentValue + value;
        goalDifferenceMap.put(username, currentValue);
    }

    private Integer calculateGoalDifferenceFor(Integer valueOne, Integer valueTwo) {
        if (valueOne == null || valueTwo == null) {
            throw new IllegalStateException("One of the given value is not which is not allowed! valueOne=" + valueOne + ", valueTwo=" + valueTwo);
        }
        return Math.abs(valueOne - valueTwo);
    }

    public Integer getTotalGoalDifferenceForUser(String username) {
        Integer result = goalDifferenceMap.get(username);
        return result != null ? result : 0;
    }
}

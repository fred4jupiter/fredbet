package de.fred4jupiter.fredbet.service;

import java.util.HashMap;
import java.util.Map;

public class UserRankingContainer {

    private Map<String, Integer> goalDifferenceMap = new HashMap<>();

    private Map<String, Integer> correctResultCountMap = new HashMap<>();

    public void addCorrectResultCountForUser(String username, Integer correctResultCount) {
        this.correctResultCountMap.put(username, correctResultCount);
    }

    public Integer getCorrectResultCoundForUser(String username) {
        return this.correctResultCountMap.get(username);
    }

    public void addGoalDifferenceForUser(String username, Integer goalDifference) {
        this.goalDifferenceMap.put(username, goalDifference);
    }

    public Integer getGoalDifferenceForUser(String username) {
        return this.goalDifferenceMap.get(username);
    }
}

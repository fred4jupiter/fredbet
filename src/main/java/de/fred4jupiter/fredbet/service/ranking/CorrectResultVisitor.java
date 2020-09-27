package de.fred4jupiter.fredbet.service.ranking;

import de.fred4jupiter.fredbet.domain.Bet;

import java.util.HashMap;
import java.util.Map;

class CorrectResultVisitor implements Visitor {

    private final Map<String, Integer> correctResultCountMap = new HashMap<>();

    @Override
    public void visit(Bet bet) {
        if (bet.isCorrectResult()) {
            addOneCorrectResultForUser(bet.getUserName());
        }
    }

    private void addOneCorrectResultForUser(String username) {
        Integer currentValue = correctResultCountMap.get(username);
        if (currentValue == null) {
            currentValue = 0;
        }
        currentValue = currentValue + 1;
        correctResultCountMap.put(username, currentValue);
    }

    public Integer getTotalCorrectResultCountForUser(String userName) {
        Integer result = correctResultCountMap.get(userName);
        return result != null ? result : 0;
    }
}

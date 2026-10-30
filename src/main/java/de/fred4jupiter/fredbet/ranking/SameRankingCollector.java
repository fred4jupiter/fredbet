package de.fred4jupiter.fredbet.ranking;

import org.apache.commons.collections4.ListValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
class SameRankingCollector {

    void markEntriesWithSameRanking(List<UsernamePoints> rankings) {
        final ListValuedMap<Integer, UsernamePoints> map = new ArrayListValuedHashMap<>();

        rankings.forEach(usernamePoints -> map.put(usernamePoints.getUniqueHash(), usernamePoints));

        map.keySet().forEach(key -> {
            Collection<UsernamePoints> valuesOfKey = map.get(key);
            if (valuesOfKey.size() > 1) {
                valuesOfKey.forEach(usernamePoints -> usernamePoints.setSameRankingPositionAsOtherUser(true));
            }
        });
    }

    /**
     * Assigns the position to each entry of the sorted rankings. Users with the same ranking get the same position,
     * the following user skips the shared positions (1, 2, 2, 4).
     */
    void assignRankPositions(List<UsernamePoints> sortedRankings) {
        for (int i = 0; i < sortedRankings.size(); i++) {
            UsernamePoints current = sortedRankings.get(i);
            boolean sameAsPrevious = i > 0 && current.getUniqueHash() == sortedRankings.get(i - 1).getUniqueHash();
            current.setRankPosition(sameAsPrevious ? sortedRankings.get(i - 1).getRankPosition() : i + 1);
        }
    }
}

package de.fred4jupiter.fredbet.service.ranking;

import de.fred4jupiter.fredbet.repository.UsernamePoints;
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
}

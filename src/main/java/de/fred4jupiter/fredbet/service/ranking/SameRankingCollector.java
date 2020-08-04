package de.fred4jupiter.fredbet.service.ranking;

import de.fred4jupiter.fredbet.repository.UsernamePoints;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class SameRankingCollector {

    private final Set<Integer> hashList = new HashSet<>();

    private final Set<Integer> duplicates = new HashSet<>();

    void markEntriesWithSameRanking(List<UsernamePoints> rankings) {
        for (UsernamePoints usernamePoints : rankings) {
            int uniqueHash = usernamePoints.getUniqueHash();
            if (!hashList.contains(uniqueHash)) {
                hashList.add(uniqueHash);
            } else {
                duplicates.add(uniqueHash);
            }
        }

        for (UsernamePoints usernamePoints : rankings) {
            if (duplicates.contains(usernamePoints.getUniqueHash())) {
                usernamePoints.setSameRankingPositionAsOtherUser(true);
            }
        }
    }
}

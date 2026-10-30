package de.fred4jupiter.fredbet.web.ranking;

import de.fred4jupiter.fredbet.ranking.UsernamePoints;

import java.util.List;

/**
 * Position of the current user in the ranking, shown next to the ranking list.
 *
 * @param nextPosition      the next lower position, null if the user is on the last position
 * @param pointsAheadOfNext points ahead of the next lower position, null if the user is on the last position
 */
public record RankingSummary(int position, int points, int pointsBehindLeader, Integer nextPosition, Integer pointsAheadOfNext) {

    /**
     * @return the summary for the given user or null if the user is not in the ranking
     */
    public static RankingSummary of(List<UsernamePoints> rankings, String username) {
        for (int i = 0; i < rankings.size(); i++) {
            final UsernamePoints own = rankings.get(i);
            if (!own.getUserName().equals(username)) {
                continue;
            }

            final int leaderPoints = rankings.getFirst().getTotalPoints();
            // users sharing the position of the current user are skipped
            final UsernamePoints next = rankings.subList(i + 1, rankings.size()).stream()
                .filter(ranking -> ranking.getRankPosition() > own.getRankPosition()).findFirst().orElse(null);
            return new RankingSummary(own.getRankPosition(), own.getTotalPoints(), leaderPoints - own.getTotalPoints(),
                next != null ? next.getRankPosition() : null, next != null ? own.getTotalPoints() - next.getTotalPoints() : null);
        }
        return null;
    }

    public boolean leading() {
        return position == 1;
    }
}

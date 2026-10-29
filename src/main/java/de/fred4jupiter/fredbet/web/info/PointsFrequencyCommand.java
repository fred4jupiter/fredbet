package de.fred4jupiter.fredbet.web.info;

import de.fred4jupiter.fredbet.excel.PointCountResult;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Matrix of how often each user has reached a certain number of points in a bet.
 *
 * <p>The users are sorted like a medal table: by the count of the highest points first, then by the count of the
 * next lower points and so on.
 */
public class PointsFrequencyCommand {

    private final List<Integer> pointsList;

    private final List<UserPointsFrequency> rows;

    private final Map<Integer, Long> maxCountByPoints = new HashMap<>();

    public PointsFrequencyCommand(Collection<PointCountResult> results) {
        this.pointsList = results.stream().map(PointCountResult::points).filter(Objects::nonNull).filter(points -> points > 0)
            .distinct().sorted(Comparator.reverseOrder()).toList();

        Map<String, Map<Integer, Long>> countsByUser = new LinkedHashMap<>();
        for (PointCountResult result : results) {
            Map<Integer, Long> counts = countsByUser.computeIfAbsent(result.username(), username -> new HashMap<>());
            if (result.points() != null && result.points() > 0) {
                counts.put(result.points(), result.numberOfPointsCount());
                maxCountByPoints.merge(result.points(), result.numberOfPointsCount(), Math::max);
            }
        }

        this.rows = countsByUser.entrySet().stream()
            .map(entry -> new UserPointsFrequency(entry.getKey(), entry.getValue()))
            .sorted(medalTableComparator().thenComparing(UserPointsFrequency::username, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }

    private Comparator<UserPointsFrequency> medalTableComparator() {
        Comparator<UserPointsFrequency> comparator = (a, b) -> 0;
        for (Integer points : pointsList) {
            comparator = comparator.thenComparing(row -> row.getCount(points), Comparator.reverseOrder());
        }
        return comparator;
    }

    public List<Integer> getPointsList() {
        return pointsList;
    }

    public List<UserPointsFrequency> getRows() {
        return rows;
    }

    public boolean isMaxCount(Integer points, long count) {
        return count > 0 && count == maxCountByPoints.getOrDefault(points, 0L);
    }

    public record UserPointsFrequency(String username, Map<Integer, Long> counts) {

        public long getCount(Integer points) {
            return counts.getOrDefault(points, 0L);
        }
    }
}

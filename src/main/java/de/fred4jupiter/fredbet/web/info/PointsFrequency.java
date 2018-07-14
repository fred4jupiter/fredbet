package de.fred4jupiter.fredbet.web.info;

import de.fred4jupiter.fredbet.repository.PointCountResult;

import java.util.Comparator;
import java.util.List;

public class PointsFrequency {

    private final Integer points;

    private final List<PointCountResult> list;

    public PointsFrequency(Integer points, List<PointCountResult> list) {
        this.points = points;
        this.list = list;

        Comparator<PointCountResult> comparator1 = Comparator.comparing(PointCountResult::getNumberOfPointsCount).reversed();
        Comparator<PointCountResult> comparator2 = Comparator.comparing(PointCountResult::getUsername, String.CASE_INSENSITIVE_ORDER);
        this.list.sort(comparator1.thenComparing(comparator2));
    }

    public Integer getPoints() {
        return points;
    }

    public List<PointCountResult> getList() {
        return list;
    }
}

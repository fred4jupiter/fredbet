package de.fred4jupiter.fredbet.service.excel;

import java.util.*;
import java.util.stream.Collectors;

public class PointsFrequencyContainer {

    private final Map<String, Map<Integer, Integer>> map = new HashMap<>();

    private final List<Integer> pointsList = new ArrayList<>();

    public void add(String username, Integer points, Integer numberOfPointsCount) {
        map.computeIfAbsent(username, k -> new HashMap<>());

        Map<Integer, Integer> valueMap = map.get(username);
        if (valueMap.get(points) == null) {
            valueMap.put(points, numberOfPointsCount);
        } else {
            Integer value = valueMap.get(numberOfPointsCount);
            valueMap.put(points, value + numberOfPointsCount);
        }

        if (!pointsList.contains(points) && !points.equals(0)) {
            pointsList.add(points);
        }
    }

    public void iterateResult(ResultCallback resultCallback) {
        Set<String> usernames = map.keySet();
        for (String username : usernames) {
            Map<Integer, Integer> pointsMap = map.get(username);

            final List<Integer> numberOfPointsCountList = new ArrayList<>();
            for (Integer points : getNumberOfPointsCountList()) {
                numberOfPointsCountList.add(pointsMap.get(points));
            }
            resultCallback.doWith(username, numberOfPointsCountList);
        }
    }

    public List<Integer> getNumberOfPointsCountList() {
        return pointsList.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @FunctionalInterface
    public interface ResultCallback {

        void doWith(String username, List<Integer> pointsList);
    }
}

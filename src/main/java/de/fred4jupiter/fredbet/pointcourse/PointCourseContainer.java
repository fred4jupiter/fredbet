package de.fred4jupiter.fredbet.pointcourse;

import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;

import java.util.*;
import java.util.function.BiConsumer;

class PointCourseContainer {

    private final List<String> matchLabels = new ArrayList<>();

    private final Map<String, LinkedList<Integer>> map = new HashMap<>();

    public PointCourseContainer(MessageSourceUtil messageSourceUtil, Locale locale, List<Match> matches) {
        matches.forEach(match -> {
            String label = match.getLabel(messageSourceUtil, locale);
            if (!matchLabels.contains(label)) {
                matchLabels.add(label);
            }
        });
    }

    public void add(String username, Integer points) {
        map.computeIfAbsent(username, k -> new LinkedList<>());

        LinkedList<Integer> values = map.get(username);
        Integer last = values.peekLast();
        if (last == null) {
            values.addLast(points);
        } else {
            values.addLast(last + points);
        }
    }

    private void iteratePointsPerUser(BiConsumer<String, List<Integer>> consumer) {
        Set<String> usernames = map.keySet();
        for (String username : usernames) {
            LinkedList<Integer> pointsPerUser = map.get(username);
            consumer.accept(username, pointsPerUser);
        }
    }

    public List<String> getLabels() {
        return new ArrayList<>(this.matchLabels);
    }

    public ChartData createChartData() {
        ChartData chartData = new ChartData(this.getLabels());
        iteratePointsPerUser(chartData::addDataSet);
        return chartData;
    }
}

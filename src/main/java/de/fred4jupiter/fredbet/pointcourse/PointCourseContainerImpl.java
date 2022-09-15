package de.fred4jupiter.fredbet.pointcourse;

import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import de.fred4jupiter.fredbet.web.info.pointcourse.ChartData;

import java.util.*;

class PointCourseContainerImpl implements PointCourseContainer {

    private final List<String> matchLabels = new ArrayList<>();

    private final Map<String, LinkedList<Integer>> map = new HashMap<>();

    public PointCourseContainerImpl(MessageSourceUtil messageSourceUtil, Locale locale, List<Match> matches) {
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

    private void iteratePointsPerUser(PointCourseContainerImpl.ResultCallback resultCallback) {
        Set<String> usernames = map.keySet();
        for (String username : usernames) {
            LinkedList<Integer> pointsPerUser = map.get(username);
            resultCallback.doWith(username, pointsPerUser);
        }
    }

    public List<String> getLabels() {
        return new ArrayList<>(this.matchLabels);
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public ChartData createChartData() {
        ChartData chartData = new ChartData(this.getLabels());
        iteratePointsPerUser(chartData::addDataSet);
        return chartData;
    }

    @FunctionalInterface
    public interface ResultCallback {

        void doWith(String username, List<Integer> pointsList);
    }
}

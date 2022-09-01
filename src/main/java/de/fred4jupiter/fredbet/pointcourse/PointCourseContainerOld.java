package de.fred4jupiter.fredbet.pointcourse;

import de.fred4jupiter.fredbet.repository.PointCourseResult;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import de.fred4jupiter.fredbet.web.info.pointcourse.ChartData;

import java.util.*;

@Deprecated
public class PointCourseContainerOld implements PointCourseContainer {

    private final List<String> labels = new ArrayList<>();

    private final Map<String, LinkedList<Integer>> map = new HashMap<>();

    public void add(PointCourseResult pointCourseResult, MessageSourceUtil messageSourceUtil, Locale locale) {
        map.computeIfAbsent(pointCourseResult.getUsername(), k -> new LinkedList<>());

        String label = pointCourseResult.getMatch().getLabel(messageSourceUtil, locale);
        if (!labels.contains(label)) {
            labels.add(label);
        }

        LinkedList<Integer> values = map.get(pointCourseResult.getUsername());
        Integer last = values.peekLast();
        if (last == null) {
            values.addLast(pointCourseResult.getPoints());
        } else {
            values.addLast(last + pointCourseResult.getPoints());
        }
    }

    private void iteratePointsPerUser(PointCourseContainerOld.ResultCallback resultCallback) {
        Set<String> usernames = map.keySet();
        for (String username : usernames) {
            LinkedList<Integer> pointsPerUser = map.get(username);
            resultCallback.doWith(username, pointsPerUser);
        }
    }

    @Override
    public ChartData createChartData() {
        ChartData chartData = new ChartData(this.getLabels());
        iteratePointsPerUser(chartData::addDataSet);
        return chartData;
    }

    public List<String> getLabels() {
        return new ArrayList<>(this.labels);
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @FunctionalInterface
    public interface ResultCallback {

        void doWith(String username, List<Integer> pointsList);
    }
}

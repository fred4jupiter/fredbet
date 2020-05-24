package de.fred4jupiter.fredbet.service.excel;

import de.fred4jupiter.fredbet.repository.PointCourseResult;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;

import java.util.*;

public class PointCourseContainer {

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

    public void iteratePointsPerUser(PointCourseContainer.ResultCallback resultCallback) {
        Set<String> usernames = map.keySet();
        for (String username : usernames) {
            LinkedList<Integer> pointsPerUser = map.get(username);
            resultCallback.doWith(username, pointsPerUser);
        }
    }

    public List<String> getLabels() {
        return new ArrayList<>(this.labels);
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @FunctionalInterface
    public interface ResultCallback {

        void doWith(String username, List<Integer> pointsList);
    }
}

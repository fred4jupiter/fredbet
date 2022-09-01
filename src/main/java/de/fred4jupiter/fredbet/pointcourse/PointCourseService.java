package de.fred4jupiter.fredbet.pointcourse;

import de.fred4jupiter.fredbet.props.FredbetConstants;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.repository.PointCourseResult;
import de.fred4jupiter.fredbet.repository.PointsPerUser;
import de.fred4jupiter.fredbet.service.excel.PointCourseContainer;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.util.*;

// Punkteverlauf
@Service
public class PointCourseService {

    private final BetRepository betRepository;

    private final MessageSourceUtil messageSourceUtil;

    public PointCourseService(BetRepository betRepository, MessageSourceUtil messageSourceUtil) {
        this.betRepository = betRepository;
        this.messageSourceUtil = messageSourceUtil;
    }

    public PointCourseContainer reportPointsCourse(String username, Locale locale) {
        final List<PointsPerUser> pointsPerUsers = this.betRepository.queryPointsPerUser();
        final ImmutablePair<String, String> pair = calculateMinMaxPointsUsernames(pointsPerUsers);

        final PointCourseContainer pointCourseContainer = new PointCourseContainer();
        final List<PointCourseResult> pointCourseResultList = queryPointCourseResultList(username, pair);
        pointCourseResultList.forEach(pointCourseResult -> {
            if (pointCourseResult.getMatch().hasResultSet()) {
                pointCourseContainer.add(pointCourseResult, messageSourceUtil, locale);
            }
        });
        return pointCourseContainer;
    }

    private List<PointCourseResult> queryPointCourseResultList(String username, ImmutablePair<String, String> pair) {
        if (pair != null) {
            return this.betRepository.queryPointsCourse(Arrays.asList(pair.getLeft(), username, pair.getRight()));
        } else {
            return this.betRepository.queryPointsCourse(Collections.singletonList(username));
        }
    }

    private ImmutablePair<String, String> calculateMinMaxPointsUsernames(List<PointsPerUser> pointsPerUsers) {
        PointsPerUser min = pointsPerUsers.stream()
                .filter(pointsPerUser -> !pointsPerUser.getUsername().equals(FredbetConstants.TECHNICAL_USERNAME))
                .min(Comparator.comparing(PointsPerUser::getPoints)).orElse(null);
        PointsPerUser max = pointsPerUsers.stream()
                .filter(pointsPerUser -> !pointsPerUser.getUsername().equals(FredbetConstants.TECHNICAL_USERNAME))
                .max(Comparator.comparing(PointsPerUser::getPoints)).orElse(null);
        return min != null && max != null ? ImmutablePair.of(min.getUsername(), max.getUsername()) : null;
    }
}

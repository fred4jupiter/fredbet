package de.fred4jupiter.fredbet.pointcourse;

import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.repository.MatchRepository;
import de.fred4jupiter.fredbet.repository.PointCourseResult;
import de.fred4jupiter.fredbet.repository.PointsPerUser;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.util.*;

// Punkteverlauf
@Service
public class PointCourseService {

    private final BetRepository betRepository;

    private final MessageSourceUtil messageSourceUtil;

    private final MatchRepository matchRepository;

    private final String adminUsername;

    public PointCourseService(BetRepository betRepository, MessageSourceUtil messageSourceUtil, MatchRepository matchRepository, FredbetProperties fredbetProperties) {
        this.betRepository = betRepository;
        this.messageSourceUtil = messageSourceUtil;
        this.matchRepository = matchRepository;
        this.adminUsername = fredbetProperties.adminUsername();
    }

    public ChartData reportPointsCourse(String username, Locale locale) {
        final List<PointsPerUser> pointsPerUsers = this.betRepository.queryPointsPerUser();
        final List<PointsPerUser> pointsPerUsersNonNull = pointsPerUsers.stream().filter(Objects::nonNull).toList();

        final ImmutablePair<String, String> pair = calculateMinMaxPointsUsernames(username, pointsPerUsersNonNull);

        final List<String> usersToDisplay = new ArrayList<>();
        usersToDisplay.add(username);
        if (pair.getLeft() != null) {
            usersToDisplay.add(pair.getLeft());
        }
        if (pair.getRight() != null) {
            usersToDisplay.add(pair.getRight());
        }

        final List<PointCourseResult> pointCourseResultList = queryPointCourseResultList(username, pair);

        final List<Match> finishedMatches = matchRepository.findFinishedMatches();
        final PointCourseContainer pointCourseContainer = new PointCourseContainer(messageSourceUtil, locale, finishedMatches);

        finishedMatches.forEach(match -> usersToDisplay.forEach(user -> {
            Optional<PointCourseResult> found = getFor(match, user, pointCourseResultList);
            if (found.isPresent()) {
                PointCourseResult pointCourseResult = found.get();
                pointCourseContainer.add(pointCourseResult.username(), pointCourseResult.points());
            } else {
                pointCourseContainer.add(user, 0);
            }
        }));

        return pointCourseContainer.createChartData();
    }

    private Optional<PointCourseResult> getFor(Match match, String user, List<PointCourseResult> pointCourseResultList) {
        return pointCourseResultList.stream().filter(result -> result.match().equals(match) && result.username().equals(user)).findAny();
    }

    private List<PointCourseResult> queryPointCourseResultList(String username, ImmutablePair<String, String> pair) {
        if (pair != null) {
            return this.betRepository.queryPointsCourse(Arrays.asList(pair.getLeft(), username, pair.getRight()));
        } else {
            return this.betRepository.queryPointsCourse(Collections.singletonList(username));
        }
    }

    private ImmutablePair<String, String> calculateMinMaxPointsUsernames(String currentUser, List<PointsPerUser> pointsPerUsers) {
        PointsPerUser min = pointsPerUsers.stream()
                .filter(pointsPerUser -> !pointsPerUser.username().equals(adminUsername))
                .filter(pointsPerUser -> !pointsPerUser.username().equals(currentUser))
                .min(Comparator.comparing(PointsPerUser::points)).orElse(null);
        PointsPerUser max = pointsPerUsers.stream()
                .filter(pointsPerUser -> !pointsPerUser.username().equals(adminUsername))
                .filter(pointsPerUser -> !pointsPerUser.username().equals(currentUser))
                .max(Comparator.comparing(PointsPerUser::points)).orElse(null);
        return min != null && max != null ? ImmutablePair.of(min.username(), max.username()) : ImmutablePair.nullPair();
    }
}


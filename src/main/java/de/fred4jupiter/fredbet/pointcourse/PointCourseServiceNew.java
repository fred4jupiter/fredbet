package de.fred4jupiter.fredbet.pointcourse;

import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.props.FredbetConstants;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.repository.MatchRepository;
import de.fred4jupiter.fredbet.repository.PointCourseResult;
import de.fred4jupiter.fredbet.repository.PointsPerUser;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PointCourseServiceNew implements PointCourseService{

    private final BetRepository betRepository;

    private final MessageSourceUtil messageSourceUtil;

    private final MatchRepository matchRepository;

    public PointCourseServiceNew(BetRepository betRepository, MessageSourceUtil messageSourceUtil, MatchRepository matchRepository) {
        this.betRepository = betRepository;
        this.messageSourceUtil = messageSourceUtil;
        this.matchRepository = matchRepository;
    }

    @Override
    public PointCourseContainer reportPointsCourse(String username, Locale locale) {
        final List<PointsPerUser> pointsPerUsers = this.betRepository.queryPointsPerUser();
        final ImmutablePair<String, String> pair = calculateMinMaxPointsUsernames(pointsPerUsers);

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
        final PointCourseContainerNew pointCourseContainer = new PointCourseContainerNew(messageSourceUtil, locale, finishedMatches);

        finishedMatches.forEach(match -> {
            usersToDisplay.forEach(user -> {
                Optional<PointCourseResult> found = getFor(match, user, pointCourseResultList);
                if (found.isPresent()) {
                    PointCourseResult pointCourseResult = found.get();
                    pointCourseContainer.add(pointCourseResult.getUsername(), pointCourseResult.getPoints());
                } else {
                    pointCourseContainer.add(user, 0);
                }
            });
        });

        return pointCourseContainer;
    }

    private Optional<PointCourseResult> getFor(Match match, String user, List<PointCourseResult> pointCourseResultList) {
        return pointCourseResultList.stream().filter(result -> result.getMatch().equals(match) && result.getUsername().equals(user)).findAny();
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
        return min != null && max != null ? ImmutablePair.of(min.getUsername(), max.getUsername()) : ImmutablePair.nullPair();
    }
}


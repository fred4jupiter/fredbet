package de.fred4jupiter.fredbet.statistic;

import de.fred4jupiter.fredbet.betting.repository.ExtraBetRepository;
import de.fred4jupiter.fredbet.domain.entity.ExtraBet;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.team.TeamService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticService {

    private final StatisticRepository statisticRepository;

    private final ExtraBetRepository extraBetRepository;

    private final TeamService teamService;

    public StatisticService(StatisticRepository statisticRepository, TeamService teamService,
                            ExtraBetRepository extraBetRepository) {
        this.statisticRepository = statisticRepository;
        this.teamService = teamService;
        this.extraBetRepository = extraBetRepository;
    }

    public Team getFavouriteTeam() {
        return teamService.findFavouriteTeam();
    }

    public List<Statistic> createStatistic() {
        final List<Statistic> statisticList = new ArrayList<>(statisticRepository.createStatistic());

        final Team favouriteTeam = getFavouriteTeam();
        final Map<String, Integer> favouriteTeamPointsPerUserMap = statisticRepository
            .sumPointsPerUserForFavouriteTeam(favouriteTeam);
        final Optional<Integer> maxFavouriteTeamPoints = favouriteTeamPointsPerUserMap.values().stream()
            .max(Comparator.comparing(i -> i));

        final Map<String, Integer> extraBetsPerUser = findExtraBetsPerUser();

        final StatisticAgg statisticAgg = calculateMinMax(statisticList);

        for (Statistic statistic : statisticList) {
            statistic.setFavouriteTeam(favouriteTeam);

            final Integer favouriteTeamPoints = favouriteTeamPointsPerUserMap.get(statistic.getUsername());
            if (favouriteTeamPoints != null) {
                statistic.setPointsFavouriteTeam(favouriteTeamPoints);
                if (maxFavouriteTeamPoints.isPresent() && favouriteTeamPoints.equals(maxFavouriteTeamPoints.get())
                    && maxFavouriteTeamPoints.get() > 0) {
                    statistic.setMaxFavouriteTeamCandidate(true);
                }
            }

            statistic.setPointsForExtraBets(extraBetsPerUser.get(statistic.getUsername()));

            // as long as everybody has the same points, there is neither a top nor a flop better
            if (statisticAgg.minPoints() < statisticAgg.maxPoints()) {
                if (statistic.getSum() == statisticAgg.minPoints()) {
                    statistic.setMinPointsCandidate(true);
                }
                if (statistic.getSum() == statisticAgg.maxPoints()) {
                    statistic.setMaxPointsCandidate(true);
                }
            }
            if (statisticAgg.maxGroupPoints() > 0 && statistic.getPointsGroup() == statisticAgg.maxGroupPoints()) {
                statistic.setMaxGroupPointsCandidate(true);
            }
        }

        statisticList.sort(Comparator.comparing(Statistic::getSum, Comparator.reverseOrder())
            .thenComparing(Statistic::getUsername, String.CASE_INSENSITIVE_ORDER));
        return statisticList;
    }

    private StatisticAgg calculateMinMax(List<Statistic> statisticList) {
        int minPoints = Integer.MAX_VALUE;
        int maxPoints = 0;
        int maxGroupPoints = 0;

        for (Statistic statistic : statisticList) {
            if (statistic.getSum() < minPoints) {
                minPoints = statistic.getSum();
            }
            if (statistic.getSum() > maxPoints) {
                maxPoints = statistic.getSum();
            }

            if (statistic.getPointsGroup() > maxGroupPoints) {
                maxGroupPoints = statistic.getPointsGroup();
            }
        }

        return new StatisticAgg(minPoints, maxPoints, maxGroupPoints);
    }

    private Map<String, Integer> findExtraBetsPerUser() {
        List<ExtraBet> extraBets = extraBetRepository.findAll();
        return extraBets.stream().collect(Collectors.toMap(ExtraBet::getUserName, ExtraBet::getPoints));
    }
}

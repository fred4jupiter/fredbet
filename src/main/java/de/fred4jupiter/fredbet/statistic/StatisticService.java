package de.fred4jupiter.fredbet.statistic;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.ExtraBet;
import de.fred4jupiter.fredbet.repository.ExtraBetRepository;
import de.fred4jupiter.fredbet.service.config.RuntimeSettingsService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StatisticService {

    private final StatisticRepository statisticRepository;

    private final ExtraBetRepository extraBetRepository;

    private final RuntimeSettingsService runtimeSettingsService;

    public StatisticService(StatisticRepository statisticRepository, RuntimeSettingsService runtimeSettingsService,
                            ExtraBetRepository extraBetRepository) {
        this.statisticRepository = statisticRepository;
        this.runtimeSettingsService = runtimeSettingsService;
        this.extraBetRepository = extraBetRepository;
    }

    public Country getFavouriteCountry() {
        return runtimeSettingsService.loadRuntimeSettings().getFavouriteCountry();
    }

    public List<Statistic> createStatistic() {
        final List<Statistic> statisticList = statisticRepository.createStatistic();

        final Country favouriteCountry = getFavouriteCountry();
        final Map<String, Integer> favoriteCountryPointsPerUserMap = statisticRepository
                .sumPointsPerUserForFavoriteCountry(favouriteCountry);
        final Optional<Integer> maxFavoriteCountryPoints = favoriteCountryPointsPerUserMap.values().stream()
                .max(Comparator.comparing(i -> i));

        final Map<String, Integer> extraBetsPerUser = findExtraBetsPerUser();

        final StatisticAgg statisticAgg = calculateMinMax(statisticList);

        for (Statistic statistic : statisticList) {
            statistic.setFavoriteCountry(favouriteCountry);

            final Integer favoriteCountryPoints = favoriteCountryPointsPerUserMap.get(statistic.getUsername());
            if (favoriteCountryPoints != null) {
                statistic.setPointsFavoriteCountry(favoriteCountryPoints);
                if (maxFavoriteCountryPoints.isPresent() && favoriteCountryPoints.equals(maxFavoriteCountryPoints.get())) {
                    if (maxFavoriteCountryPoints.get() > 0) {
                        statistic.setMaxFavoriteCountryCandidate(true);
                    }
                }
            }

            statistic.setPointsForExtraBets(extraBetsPerUser.get(statistic.getUsername()));

            if (statistic.getSum() == statisticAgg.minPoints()) {
                statistic.setMinPointsCandidate(true);
            }
            if (statistic.getSum() == statisticAgg.maxPoints()) {
                statistic.setMaxPointsCandidate(true);
            }
            if (statistic.getPointsGroup() == statisticAgg.maxGroupPoints()) {
                statistic.setMaxGroupPointsCandidate(true);
            }
        }

        statisticList.sort(Comparator.comparing(Statistic::getUsername, String.CASE_INSENSITIVE_ORDER));
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

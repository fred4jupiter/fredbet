package de.fred4jupiter.fredbet.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Statistic;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.repository.StatisticRepository;

@Service
public class StatisticService {

	private final StatisticRepository statisticRepository;

	private final Country favoriteCountry;

	@Autowired
	public StatisticService(StatisticRepository statisticRepository, FredbetProperties fredbetProperties) {
		this.statisticRepository = statisticRepository;
		this.favoriteCountry = fredbetProperties.getFavouriteCountry();
		Assert.notNull(this.favoriteCountry);
	}

	public List<Statistic> createStatistic() {
		final List<Statistic> statisticList = statisticRepository.createStatistic();

		final Map<String, Integer> favoriteCountryPointsPerUserMap = statisticRepository
				.sumPointsPerUserForFavoriteCountry(favoriteCountry);
		final Optional<Integer> maxFavoriteCountryPoints = favoriteCountryPointsPerUserMap.values().stream()
				.max(Comparator.comparing(i -> i));
		
		int minPoints = Integer.MAX_VALUE;
		int maxPoints = 0;
		int maxGroupPoints = 0;

		for (Statistic statistic : statisticList) {
			if (statistic.getSum().intValue() < minPoints) {
				minPoints = statistic.getSum().intValue();
			}
			if (statistic.getSum().intValue() > maxPoints) {
				maxPoints = statistic.getSum().intValue();
			}

			if (statistic.getPointsGroup().intValue() > maxGroupPoints) {
				maxGroupPoints = statistic.getPointsGroup().intValue();
			}
		}
		
		for (Statistic statistic : statisticList) {
			statistic.setFavoriteCountry(this.favoriteCountry);
			
			final Integer favoriteCountryPoints = favoriteCountryPointsPerUserMap.get(statistic.getUsername());
			statistic.setPointsFavoriteCountry(favoriteCountryPoints);
			
			if (maxFavoriteCountryPoints.isPresent() && favoriteCountryPoints.equals(maxFavoriteCountryPoints.get())) {
				statistic.setMaxFavoriteCountryCandidate(true);
			}
			
			if (statistic.getSum().intValue() == minPoints) {
				statistic.setMinPointsCandidate(true);
			}
			if (statistic.getSum().intValue() == maxPoints) {
				statistic.setMaxPointsCandidate(true);
			}
			if (statistic.getPointsGroup().intValue() == maxGroupPoints) {
				statistic.setMaxGroupPointsCandidate(true);
			}
		}

		return statisticList;
	}
}

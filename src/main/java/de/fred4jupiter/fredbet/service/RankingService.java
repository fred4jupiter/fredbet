package de.fred4jupiter.fredbet.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.RankingSelection;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.repository.UsernamePoints;

@Service
public class RankingService {

	@Autowired
	private BetRepository betRepository;

	@Autowired
	private ChildRelationFetcher childRelationFetcher;

	public List<UsernamePoints> calculateCurrentRanking(RankingSelection rankingSelection) {
		if (RankingSelection.MIXED.equals(rankingSelection)) {
			return betRepository.calculateRanging();
		}

		Map<String, Boolean> relationMap = childRelationFetcher.fetchUserIsChildRelation();

		final List<UsernamePoints> ranking = betRepository.calculateRanging();

		if (RankingSelection.ONLY_ADULTS.equals(rankingSelection)) {
			return ranking.stream().filter(usernamePoints -> !relationMap.get(usernamePoints.getUserName())).collect(Collectors.toList());
		} else if (RankingSelection.ONLY_CHILDREN.equals(rankingSelection)) {
			return ranking.stream().filter(usernamePoints -> relationMap.get(usernamePoints.getUserName())).collect(Collectors.toList());
		} else {
			throw new IllegalArgumentException("Unsupported ranking selection " + rankingSelection);
		}
	}
}

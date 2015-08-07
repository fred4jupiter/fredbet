package de.fred4jupiter.fredbet.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.repository.BetRepository;

@Service
public class PointsCalculationService {

	private static final Logger LOG = LoggerFactory.getLogger(PointsCalculationService.class);
	
	@Autowired
	private BetRepository betRepository;
	
	public Integer calculatePointsFor(Match match, Bet bet) {
		if (isSameGoalResult(match, bet)) {
			return 3;
		}
		
		if (isSameGoalDifference(match, bet)) {
			return 2;
		}
		
		if (isCorrectWinner(match, bet)) {
			return 1;
		}
		return 0;
	}

	private boolean isCorrectWinner(Match match, Bet bet) {
		return (match.isTeamOneWinner() && bet.isTeamOneWinner()) || (match.isTeamTwoWinner() && bet.isTeamTwoWinner());
	}

	private boolean isSameGoalDifference(Match match, Bet bet) {
		return match.getGoalDifference().intValue() == bet.getGoalDifference().intValue();
	}

	private boolean isSameGoalResult(Match match, Bet bet) {
		return match.getGoalsTeamOne().equals(bet.getGoalsTeamOne()) && match.getGoalsTeamTwo().equals(bet.getGoalsTeamTwo());
	}

	public void calculatePointsForAllBets() {
		List<Bet> allBets = betRepository.findAll();
		for (Bet bet : allBets) {
			Integer points = calculatePointsFor(bet.getMatch(), bet);
			bet.setPoints(points);
			LOG.debug("User {} gets {} points", bet.getUserName(), points);
		}
		
		betRepository.save(allBets);
	}
}

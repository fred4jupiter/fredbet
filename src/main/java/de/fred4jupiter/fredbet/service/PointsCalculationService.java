package de.fred4jupiter.fredbet.service;

import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Match;

@Service
public class PointsCalculationService {

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
}

package de.fred4jupiter.fredbet.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.repository.BetRepository;

/**
 * Calculates the general betting points (but without extra bets points).
 * 
 * @author michael
 *
 */
@Service
public class PointsCalculationService implements ApplicationListener<MatchGoalsChangedEvent> {

	private static final int JOKER_MULTIPLIER = 2;

	private static final Logger LOG = LoggerFactory.getLogger(PointsCalculationService.class);

	@Autowired
	private BetRepository betRepository;

	@Override
	public void onApplicationEvent(MatchGoalsChangedEvent event) {
		final Match match = event.getMatch();

		LOG.debug("match={} has finished. Calculating points for bets...", match);
		calculatePointsFor(match);
	}

	void calculatePointsFor(final Match match) {
		List<Bet> allBetsForThisMatch = betRepository.findByMatch(match);

		allBetsForThisMatch.forEach(bet -> {
			if (match.hasResultSet()) {
				bet.setPoints(calculatePointsFor(match, bet));
			} else {
				bet.setPoints(null);
			}

			LOG.debug("User {} gets {} points", bet.getUserName(), bet.getPoints());
		});

		betRepository.saveAll(allBetsForThisMatch);
	}

	int calculatePointsFor(Match match, Bet bet) {
		final int standardPoints = calculateStandardPointsFor(match, bet);
		final int penaltyPoints = calculatePenaltyPointsFor(match, bet);
		
		final int subtotal = standardPoints + penaltyPoints;
		if (bet.isJoker()) {
			return subtotal * JOKER_MULTIPLIER;
		}
		
		return subtotal;
	}

	private int calculatePenaltyPointsFor(Match match, Bet bet) {
		if (match.isGroupMatch()) {
			return 0;
		}
		
		if (match.isUndecidedResult() && bet.isUndecidedBetting()) {
			if (match.isPenaltyWinnerOne() && bet.isPenaltyWinnerOne()) {
				return 1;
			}

			if (!match.isPenaltyWinnerOne() && !bet.isPenaltyWinnerOne()) {
				return 1;
			}
		}

		return 0;
	}

	private int calculateStandardPointsFor(Match match, Bet bet) {
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
		if (match.isTeamOneWinner() && bet.isTeamTwoWinner()) {
			return false;
		}
		if (match.isTeamTwoWinner() && bet.isTeamOneWinner()) {
			return false;
		}

		return match.getGoalDifference().intValue() == bet.getGoalDifference().intValue();
	}

	private boolean isSameGoalResult(Match match, Bet bet) {
		Assert.notNull(match.getGoalsTeamOne(), "no goals team one given");
		Assert.notNull(match.getGoalsTeamTwo(), "no goals team two given");
		return match.getGoalsTeamOne().equals(bet.getGoalsTeamOne()) && match.getGoalsTeamTwo().equals(bet.getGoalsTeamTwo());
	}

}

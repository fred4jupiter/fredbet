package de.fred4jupiter.fredbet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Joker;
import de.fred4jupiter.fredbet.repository.BetRepository;

@Service
public class JokerService {

	private static final Integer MAX_NUMBER_OF_JOKERS = 3;

	@Autowired
	private BetRepository betRepository;

	public Joker getJokerForUser(String userName) {
		Integer numberOfJokersUsed = betRepository.countNumberOfJokerUsed(userName);
		return new Joker(numberOfJokersUsed, MAX_NUMBER_OF_JOKERS);
	}

	public boolean isSettingJokerAllowed(String userName, Long matchId) {
		Joker joker = getJokerForUser(userName);
		if (joker.getNumberOfJokersUsed().intValue() < joker.getMax().intValue()) {
			return true;
		}

		Bet bet = betRepository.findBetsOfGivenMatchWithJokerSet(userName, matchId);
		if (bet != null) {
			// This bet is one of the bets with the previous set joker. So you can edit this
			return true;
		}

		return false;
	}

}

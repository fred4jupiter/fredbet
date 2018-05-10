package de.fred4jupiter.fredbet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Joker;
import de.fred4jupiter.fredbet.domain.RuntimeConfig;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.service.config.RuntimeConfigurationService;

@Service
public class JokerService {

	@Autowired
	private BetRepository betRepository;

	@Autowired
	private RuntimeConfigurationService runtimeConfigurationService;

	public Joker getJokerForUser(String userName) {
		Integer numberOfJokersUsed = betRepository.countNumberOfJokerUsed(userName);
		RuntimeConfig runtimeConfig = runtimeConfigurationService.loadRuntimeConfig();
		return new Joker(numberOfJokersUsed, runtimeConfig.getJokerMaxCount());
	}

	public boolean isSettingJokerAllowed(String userName, Long matchId) {
		Joker joker = getJokerForUser(userName);
		if (joker.getNumberOfJokersUsed().intValue() < joker.getMax().intValue()) {
			return true;
		}

		Bet bet = betRepository.findBetsOfGivenMatchWithJokerSet(userName, matchId);
		if (bet != null) {
			// This bet is one of the bets with the previous set joker. So you
			// can edit this
			return true;
		}

		return false;
	}

}

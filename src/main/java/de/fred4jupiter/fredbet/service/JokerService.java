package de.fred4jupiter.fredbet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}

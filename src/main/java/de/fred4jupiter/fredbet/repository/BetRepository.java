package de.fred4jupiter.fredbet.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Match;

public interface BetRepository extends MongoRepository<Bet, String>{

	Bet findByUserNameAndMatch(String currentUsername, Match match);

}

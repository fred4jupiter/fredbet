package de.fred4jupiter.fredbet.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Match;

public interface BetRepository extends MongoRepository<Bet, String>, BetRepositoryCustom{

	Bet findByUserNameAndMatch(String currentUsername, Match match);
	
	List<Bet> findByUserName(String currentUsername);
	
	List<Bet> findByMatch(Match match);


}

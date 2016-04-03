package de.fred4jupiter.fredbet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Match;

public interface BetRepository extends JpaRepository<Bet, Long>, BetRepositoryCustom{

	Bet findByUserNameAndMatch(String currentUsername, Match match);
	
	List<Bet> findByUserName(String currentUsername);
	
	List<Bet> findByMatch(Match match);
	
	List<Bet> findByMatchIdOrderByUserNameAsc(Long id);
	
	Long countByMatch(Match match);


}

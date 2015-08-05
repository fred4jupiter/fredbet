package de.fred4jupiter.fredbet.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.web.RankingCommand;

@Service
public class RankingService {

	@Autowired
	private BetRepository betRepository;
	
	public List<RankingCommand> calculateCurrentRanking() {
		List<Bet> bets = betRepository.findAll(new Sort(Direction.DESC, "points"));
		
		List<RankingCommand> rankingList = new ArrayList<>();
		for (Bet bet : bets) {
			rankingList.add(new RankingCommand(bet.getUserName(), bet.getPoints()));
		}
		return rankingList;
	}
}

package de.fred4jupiter.fredbet.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.repository.UsernamePoints;

@Service
public class RankingService {

	@Autowired
	private BetRepository betRepository;
	
	public List<UsernamePoints> calculateCurrentRanking() {
		return betRepository.calculateRanging();		
	}
}

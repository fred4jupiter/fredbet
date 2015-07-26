package de.fred4jupiter.fredbet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.repository.AppUserRepository;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.repository.MatchRepository;

@Service
public class BettingService {

	@Autowired
	private AppUserRepository appUserRepository;

	@Autowired
	private MatchRepository matchRepository;

	@Autowired
	private BetRepository betRepository;

	public void createBetting(String username, String matchId, Integer goalsTeamOne, Integer goalsTeamTwo) {
		AppUser appUser = appUserRepository.findByUsername(username);

		Match match = matchRepository.findOne(matchId);

		createBetting(appUser, match, goalsTeamOne, goalsTeamTwo);
	}

	public Bet createBetting(AppUser appUser, Match match, Integer goalsTeamOne, Integer goalsTeamTwo) {
		Bet bet = new Bet();
		bet.setGoalsTeamOne(goalsTeamOne);
		bet.setGoalsTeamTwo(goalsTeamTwo);
		bet.setMatch(match);
		bet.setUserName(appUser.getUsername());
		return betRepository.save(bet);
	}
}

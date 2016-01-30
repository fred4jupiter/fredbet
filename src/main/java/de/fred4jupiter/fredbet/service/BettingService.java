package de.fred4jupiter.fredbet.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.repository.AppUserRepository;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.repository.MatchRepository;
import de.fred4jupiter.fredbet.web.MessageUtil;
import de.fred4jupiter.fredbet.web.bet.BetCommand;

@Service
public class BettingService {

	@Autowired
	private AppUserRepository appUserRepository;

	@Autowired
	private MatchRepository matchRepository;

	@Autowired
	private BetRepository betRepository;
	
	@Autowired
	private MessageUtil messageUtil;

	public void createAndSaveBetting(String username, String matchId, Integer goalsTeamOne, Integer goalsTeamTwo) {
		AppUser appUser = appUserRepository.findByUsername(username);

		Match match = matchRepository.findOne(matchId);

		createAndSaveBetting(appUser, match, goalsTeamOne, goalsTeamTwo);
	}

	public Bet createAndSaveBetting(AppUser appUser, Match match, Integer goalsTeamOne, Integer goalsTeamTwo) {
		Bet bet = new Bet();
		bet.setGoalsTeamOne(goalsTeamOne);
		bet.setGoalsTeamTwo(goalsTeamTwo);
		bet.setMatch(match);
		bet.setUserName(appUser.getUsername());
		return betRepository.save(bet);
	}

	public List<Bet> findAllByUsername(String username) {
		return betRepository.findByUserName(username);
	}

	public List<Match> findMatchesToBet(String username) {
		List<Bet> userBets = betRepository.findByUserName(username);
		List<String> matchIds = userBets.stream().map(bet -> bet.getMatch().getId()).collect(Collectors.toList());

		List<Match> matchesToBet = new ArrayList<>();
		List<Match> allMatches = matchRepository.findAllByOrderByKickOffDateAsc();
		for (Match match : allMatches) {
			if (!matchIds.contains(match.getId()) && match.isBetable()) {
				matchesToBet.add(match);
			}
		}

		return matchesToBet;
	}

	public BetCommand findByBetId(String betId) {
		Bet bet = betRepository.findOne(betId);
		if (bet == null) {
		    throw new IllegalArgumentException("Could not find bet with betId="+betId);
		}
		BetCommand betCommand = mapBetToCommand(bet);
		return betCommand;
	}

	private BetCommand mapBetToCommand(Bet bet) {
		BetCommand betCommand = new BetCommand(messageUtil);
		betCommand.setBetId(bet.getId());
		betCommand.setTeamOne(bet.getMatch().getTeamOne());
		betCommand.setTeamTwo(bet.getMatch().getTeamTwo());
		betCommand.setGoalsTeamOne(bet.getGoalsTeamOne());
		betCommand.setGoalsTeamTwo(bet.getGoalsTeamTwo());
		betCommand.setMatchId(bet.getMatch().getId());
		return betCommand;
	}

	public String save(BetCommand betCommand) {
		Match match = matchRepository.findOne(betCommand.getMatchId());
		if (match.hasStarted()) {
			throw new NoBettingAfterMatchStartedAllowedException("The match has already been started! You are to late!");
		}

		Bet bet = betRepository.findOne(betCommand.getBetId());
		if (bet == null) {
			bet = new Bet();
		}

		bet.setMatch(match);
		bet.setGoalsTeamOne(betCommand.getGoalsTeamOne());
		bet.setGoalsTeamTwo(betCommand.getGoalsTeamTwo());
		bet.setUserName(getCurrentUsername());

		bet = betRepository.save(bet);
		return bet.getId();
	}

	private String getCurrentUsername() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	public BetCommand findOrCreateBetForMatch(String matchId) {
		Match match = matchRepository.findOne(matchId);
		Bet bet = betRepository.findByUserNameAndMatch(getCurrentUsername(), match);
		if (bet == null) {
			bet = new Bet();
			bet.setMatch(match);
			bet.setUserName(getCurrentUsername());
		}

		return mapBetToCommand(bet);
	}

	public void deleteAllBets() {
		betRepository.deleteAll();
	}

}

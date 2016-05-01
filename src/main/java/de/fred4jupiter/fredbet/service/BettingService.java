package de.fred4jupiter.fredbet.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.fred4jupiter.fredbet.FredbetConstants;
import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.ExtraBet;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.repository.AppUserRepository;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.repository.ExtraBetRepository;
import de.fred4jupiter.fredbet.repository.MatchRepository;
import de.fred4jupiter.fredbet.web.MessageUtil;
import de.fred4jupiter.fredbet.web.bet.AllBetsCommand;
import de.fred4jupiter.fredbet.web.bet.BetCommand;
import de.fred4jupiter.fredbet.web.bet.ExtraBetCommand;

@Service
@Transactional
public class BettingService {

	@Autowired
	private AppUserRepository appUserRepository;

	@Autowired
	private MatchRepository matchRepository;

	@Autowired
	private BetRepository betRepository;

	@Autowired
	private MessageUtil messageUtil;

	@Autowired
	private ExtraBetRepository extraBetRepository;

	public void createAndSaveBetting(String username, Long matchId, Integer goalsTeamOne, Integer goalsTeamTwo) {
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
		List<Long> matchIds = userBets.stream().map(bet -> bet.getMatch().getId()).collect(Collectors.toList());

		List<Match> matchesToBet = new ArrayList<>();
		List<Match> allMatches = matchRepository.findAllByOrderByKickOffDateAsc();
		for (Match match : allMatches) {
			if (!matchIds.contains(match.getId()) && match.isBetable()) {
				matchesToBet.add(match);
			}
		}

		return matchesToBet;
	}

	public BetCommand findByBetId(Long betId) {
		Bet bet = betRepository.findOne(betId);
		if (bet == null) {
			throw new IllegalArgumentException("Could not find bet with betId=" + betId);
		}
		BetCommand betCommand = toBetCommand(bet);
		return betCommand;
	}

	private BetCommand toBetCommand(Bet bet) {
		BetCommand betCommand = new BetCommand(messageUtil, bet);
		betCommand.setBetId(bet.getId());
		betCommand.setGoalsTeamOne(bet.getGoalsTeamOne());
		betCommand.setGoalsTeamTwo(bet.getGoalsTeamTwo());
		betCommand.setMatchId(bet.getMatch().getId());
		betCommand.setPenaltyWinnerOne(bet.isPenaltyWinnerOne());
		return betCommand;
	}

	public Long save(BetCommand betCommand) {
		Match match = matchRepository.findOne(betCommand.getMatchId());
		if (match.hasStarted()) {
			throw new NoBettingAfterMatchStartedAllowedException("The match has already been started! You are to late!");
		}

		Bet bet = null;
		if (betCommand.getBetId() != null) {
			bet = betRepository.findOne(betCommand.getBetId());
		}

		if (bet == null) {
			bet = new Bet();
		}

		bet.setMatch(match);
		bet.setGoalsTeamOne(betCommand.getGoalsTeamOne());
		bet.setGoalsTeamTwo(betCommand.getGoalsTeamTwo());
		bet.setUserName(getCurrentUsername());
		bet.setPenaltyWinnerOne(betCommand.isPenaltyWinnerOne());

		bet = betRepository.save(bet);
		return bet.getId();
	}

	private String getCurrentUsername() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	public BetCommand findOrCreateBetForMatch(Long matchId) {
		Match match = matchRepository.findOne(matchId);
		Bet bet = betRepository.findByUserNameAndMatch(getCurrentUsername(), match);
		if (bet == null) {
			bet = new Bet();
			bet.setMatch(match);
			bet.setUserName(getCurrentUsername());
		}

		return toBetCommand(bet);
	}

	public void deleteAllBets() {
		betRepository.deleteAll();
	}

	public AllBetsCommand findAllBetsForMatchId(final Long matchId) {
		Match match = matchRepository.findOne(matchId);
		List<Bet> allBets = betRepository.findByMatchIdOrderByUserNameAsc(matchId);
		List<Bet> filtered = allBets.stream().filter(bet -> !bet.getUserName().equals(FredbetConstants.TECHNICAL_USERNAME))
				.collect(Collectors.toList());

		return new AllBetsCommand(filtered, match, messageUtil);
	}

	public void saveExtraBet(Country finalWinner, Country semiFinalWinner, String username) {
		ExtraBet found = extraBetRepository.findByUserName(username);
		if (Country.NONE.equals(finalWinner) && Country.NONE.equals(semiFinalWinner) && found != null) {
			// reset/delete existing extra bet
			extraBetRepository.delete(found);
			return;
		}
		
		if (found == null) {
			found = new ExtraBet();
		}

		found.setFinalWinner(finalWinner);
		found.setSemiFinalWinner(semiFinalWinner);
		found.setUserName(username);

		extraBetRepository.save(found);
	}

	public ExtraBetCommand loadExtraBetforUser(String username) {
		ExtraBet extraBet = extraBetRepository.findByUserName(username);
		if (extraBet == null) {
			extraBet = new ExtraBet();
		}

		ExtraBetCommand extraBetCommand = new ExtraBetCommand();
		Match finalMatch = findFinalMatch();
		if (finalMatch != null) {
			extraBetCommand.setFinalMatch(finalMatch);
		}

		extraBetCommand.setExtraBetId(extraBet.getId());
		extraBetCommand.setFinalWinner(extraBet.getFinalWinner());
		extraBetCommand.setSemiFinalWinner(extraBet.getSemiFinalWinner());
		extraBetCommand.setPoints(extraBet.getPoints());
		return extraBetCommand;
	}

	private Match findFinalMatch() {
		List<Match> matches = matchRepository.findByGroup(Group.FINAL);
		if (matches == null || matches.isEmpty()) {
			return null;
		}

		if (matches.size() > 1) {
			throw new IllegalStateException("Found more than one final match!");
		}

		Match finalMatch = matches.get(0);
		return finalMatch;
	}

	public boolean hasOpenExtraBet(String currentUserName) {
		ExtraBet extraBet = extraBetRepository.findByUserName(currentUserName);
		return extraBet == null;
	}

}

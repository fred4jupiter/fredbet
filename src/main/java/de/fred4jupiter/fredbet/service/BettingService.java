package de.fred4jupiter.fredbet.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.ExtraBet;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.props.FredbetConstants;
import de.fred4jupiter.fredbet.repository.AppUserRepository;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.repository.ExtraBetRepository;
import de.fred4jupiter.fredbet.repository.MatchRepository;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.util.DateUtils;

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
	private ExtraBetRepository extraBetRepository;

	@Autowired
	private SecurityService securityService;

	public void createAndSaveBetting(String username, Long matchId, Integer goalsTeamOne, Integer goalsTeamTwo) {
		AppUser appUser = appUserRepository.findByUsername(username);

		Match match = matchRepository.getOne(matchId);

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

	public Long save(Bet bet) {
		Match match = matchRepository.getOne(bet.getMatch().getId());
		if (match.hasStarted()) {
			throw new NoBettingAfterMatchStartedAllowedException("The match has already been started! You are to late!");
		}

		if (StringUtils.isBlank(bet.getUserName())) {
			bet.setUserName(securityService.getCurrentUserName());
		}

		Bet saved = betRepository.save(bet);
		return saved.getId();
	}

	public Bet findOrCreateBetForMatch(Long matchId) {
		final Optional<Match> matchOpt = matchRepository.findById(matchId);
		if (!matchOpt.isPresent()) {
			return null;
		}
		final String currentUserName = securityService.getCurrentUserName();
		Match match = matchOpt.get();
		Bet bet = betRepository.findByUserNameAndMatch(currentUserName, match);
		if (bet == null) {
			bet = new Bet();
			bet.setMatch(match);
			bet.setUserName(currentUserName);
		}

		return bet;
	}

	public void deleteAllBets() {
		betRepository.deleteAll();
		extraBetRepository.deleteAll();
	}

	public List<Bet> findAllBetsForMatchId(final Long matchId) {
		if (matchId == null) {
			return null;
		}
		List<Bet> allBets = betRepository.findByMatchIdOrderByUserNameAsc(matchId);
		return allBets.stream().filter(bet -> !bet.getUserName().equals(FredbetConstants.TECHNICAL_USERNAME)).collect(Collectors.toList());
	}

	public void saveExtraBet(Country finalWinner, Country semiFinalWinner, Country thirdFinalWinner, String username) {
		ExtraBet found = extraBetRepository.findByUserName(username);
		if (Country.NONE.equals(finalWinner) && Country.NONE.equals(semiFinalWinner) && Country.NONE.equals(thirdFinalWinner)
				&& found != null) {
			// reset/delete existing extra bet
			extraBetRepository.delete(found);
			return;
		}

		if (found == null) {
			found = new ExtraBet();
		}

		found.setFinalWinner(finalWinner);
		found.setSemiFinalWinner(semiFinalWinner);
		found.setThirdFinalWinner(thirdFinalWinner);
		found.setUserName(username);

		extraBetRepository.save(found);
	}

	public ExtraBet loadExtraBetForUser(String username) {
		ExtraBet extraBet = extraBetRepository.findByUserName(username);
		if (extraBet == null) {
			extraBet = new ExtraBet();
		}

		return extraBet;
	}

	public boolean hasFirstMatchStarted() {
		LocalDateTime dateTimeNow = LocalDateTime.now();
		Date date = matchRepository.findStartDateOfFirstMatch();
		if (date == null) {
			return false;
		}
		LocalDateTime firstMatchKickOffDate = DateUtils.toLocalDateTime(date);
		return dateTimeNow.isAfter(firstMatchKickOffDate);
	}

	public Match findFinalMatch() {
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

	public List<ExtraBet> loadExtraBetDataOthers() {
		List<ExtraBet> allExtraBets = extraBetRepository.findAll(new Sort(Direction.ASC, "userName"));
		return allExtraBets.stream().filter(extraBet -> !extraBet.getUserName().equals(FredbetConstants.TECHNICAL_USERNAME))
				.collect(Collectors.toList());
	}

	public Bet findBetById(Long betId) {
		return betRepository.getOne(betId);
	}
	
	public Long countByMatch(Match match) {
		return betRepository.countByMatch(match);
	}
}

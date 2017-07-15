package de.fred4jupiter.fredbet.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import de.fred4jupiter.fredbet.web.bet.AllBetsCommand;
import de.fred4jupiter.fredbet.web.bet.BetCommand;
import de.fred4jupiter.fredbet.web.bet.ExtraBetCommand;

@Service
@Transactional
public class BettingService {

	private static final Logger LOG = LoggerFactory.getLogger(BettingService.class);

	@Autowired
	private AppUserRepository appUserRepository;

	@Autowired
	private MatchRepository matchRepository;

	@Autowired
	private BetRepository betRepository;

	@Autowired
	private WebMessageUtil messageUtil;

	@Autowired
	private ExtraBetRepository extraBetRepository;

	@Autowired
	private SecurityService securityService;

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

	public Long save(Bet bet) {
		Match match = matchRepository.findOne(bet.getMatch().getId());
		if (match.hasStarted()) {
			throw new NoBettingAfterMatchStartedAllowedException("The match has already been started! You are to late!");
		}

		if (StringUtils.isBlank(bet.getUserName())) {
			bet.setUserName(securityService.getCurrentUserName());
		}

		Bet saved = betRepository.save(bet);
		return saved.getId();
	}

	public BetCommand findOrCreateBetForMatch(Long matchId) {
		final Match match = matchRepository.findOne(matchId);
		if (match == null) {
			return null;
		}
		final String currentUserName = securityService.getCurrentUserName();
		Bet bet = betRepository.findByUserNameAndMatch(currentUserName, match);
		if (bet == null) {
			bet = new Bet();
			bet.setMatch(match);
			bet.setUserName(currentUserName);
		}

		return toBetCommand(bet);
	}

	public void deleteAllBets() {
		betRepository.deleteAll();
		extraBetRepository.deleteAll();
	}

	public AllBetsCommand findAllBetsForMatchId(final Long matchId) {
		if (matchId == null) {
			return null;
		}
		Match match = matchRepository.findOne(matchId);
		if (match == null) {
			LOG.warn("Match with matchId={} could not be found!", matchId);
			return null;
		}
		List<Bet> allBets = betRepository.findByMatchIdOrderByUserNameAsc(matchId);
		List<Bet> filtered = allBets.stream().filter(bet -> !bet.getUserName().equals(FredbetConstants.TECHNICAL_USERNAME))
				.collect(Collectors.toList());

		return new AllBetsCommand(filtered, match, messageUtil);
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

	public ExtraBetCommand loadExtraBetForUser(String username) {
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
		extraBetCommand.setThirdFinalWinner(extraBet.getThirdFinalWinner());
		extraBetCommand.setPoints(extraBet.getPoints());
		
		boolean firstMatchStarted = hasFirstMatchStarted();
		if (firstMatchStarted) {
			extraBetCommand.setBettable(false);
		} else {
			extraBetCommand.setBettable(true);
		}

		return extraBetCommand;
	}

	private boolean hasFirstMatchStarted() {
		LocalDateTime dateTimeNow = LocalDateTime.now();
		Date date = matchRepository.findStartDateOfFirstMatch();
		LocalDateTime firstMatchKickOffDate = DateUtils.toLocalDateTime(date);
		return dateTimeNow.isAfter(firstMatchKickOffDate);
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

	public List<ExtraBet> loadExtraBetDataOthers() {
		List<ExtraBet> allExtraBets = extraBetRepository.findAll(new Sort(Direction.ASC, "userName"));
		return allExtraBets.stream().filter(extraBet -> !extraBet.getUserName().equals(FredbetConstants.TECHNICAL_USERNAME))
				.collect(Collectors.toList());
	}

	public Bet findBetById(Long betId) {
		return betRepository.findOne(betId);
	}
}

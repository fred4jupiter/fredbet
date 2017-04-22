package de.fred4jupiter.fredbet.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.repository.MatchRepository;
import de.fred4jupiter.fredbet.util.DateUtils;
import de.fred4jupiter.fredbet.util.Validator;
import de.fred4jupiter.fredbet.web.MatchConverter;
import de.fred4jupiter.fredbet.web.matches.MatchCommand;
import de.fred4jupiter.fredbet.web.matches.MatchResultCommand;

@Service
@Transactional
public class MatchService {

	private static final Logger LOG = LoggerFactory.getLogger(MatchService.class);

	@Autowired
	private MatchRepository matchRepository;

	@Autowired
	private BettingService bettingService;

	@Autowired
	private BetRepository betRepository;

	@Autowired
	private MatchConverter matchConverter;

	@Autowired
	private ApplicationContext applicationContext;

	public List<Match> findAll() {
		return matchRepository.findAll();
	}

	public MatchCommand findByMatchId(Long matchId) {
		Assert.notNull(matchId, "matchId must be given");
		Match match = matchRepository.findOne(matchId);
		if (match == null) {
			return null;
		}
		Long numberOfBetsForThisMatch = betRepository.countByMatch(match);
		MatchCommand matchCommand = matchConverter.toMatchCommand(match);
		if (numberOfBetsForThisMatch == 0) {
			matchCommand.setDeletable(true);
		}
		return matchCommand;
	}

	public Match findMatchByMatchId(Long matchId) {
		return matchRepository.findOne(matchId);
	}

	public Match save(Match match) {
		match = matchRepository.save(match);

		if (match.hasGoalsChanged()) {
			LOG.debug("fire MatchGoalsChangedEvent...");
			applicationContext.publishEvent(new MatchGoalsChangedEvent(this, match));
		}

		return match;
	}

	public Long save(MatchCommand matchCommand) {
		Match match = null;
		if (matchCommand.getMatchId() != null) {
			match = matchRepository.findOne(matchCommand.getMatchId());
		}

		if (match == null) {
			match = new Match();
		}

		matchConverter.toMatch(matchCommand, match);

		match = save(match);
		matchCommand.setMatchId(match.getId());

		return match.getId();
	}

	public List<MatchCommand> findAllMatches(String username) {
		List<Match> allMatches = matchRepository.findAllByOrderByKickOffDateAsc();
		return toMatchCommandsWithBets(username, allMatches);
	}

	public List<MatchCommand> findAllUpcomingMatches(String username) {
	    // show current matches that has been finished since 2 hours after kick-off
	    LocalDateTime kickOffBeginSelectionDate = LocalDateTime.now().minusHours(2);
		List<Match> allMatches = matchRepository.findUpcomingMatches(DateUtils.toDate(kickOffBeginSelectionDate));
		return toMatchCommandsWithBets(username, allMatches);
	}

	private List<MatchCommand> toMatchCommandsWithBets(String username, List<Match> allMatches) {
		final Map<Long, Bet> matchToBetMap = findBetsForMatchIds(username);
		final List<MatchCommand> resultList = new ArrayList<>();
		for (Match match : allMatches) {
			MatchCommand matchCommand = matchConverter.toMatchCommand(match);
			Bet bet = matchToBetMap.get(match.getId());
			if (bet != null) {
				matchCommand.setUserBetGoalsTeamOne(bet.getGoalsTeamOne());
				matchCommand.setUserBetGoalsTeamTwo(bet.getGoalsTeamTwo());
				matchCommand.setPenaltyWinnerOneBet(bet.isPenaltyWinnerOne());				
				matchCommand.setPoints(bet.getPoints());
			}
			resultList.add(matchCommand);
		}
		return resultList;
	}

	public List<MatchCommand> findMatchesByGroup(String currentUserName, Group group) {
		List<Match> allMatches = matchRepository.findByGroupOrderByKickOffDateAsc(group);
		return toMatchCommandsWithBets(currentUserName, allMatches);
	}

	private Map<Long, Bet> findBetsForMatchIds(String username) {
		List<Bet> allUserBets = bettingService.findAllByUsername(username);
		if (Validator.isEmpty(allUserBets)) {
			LOG.debug("Could not found any bets for user: {}", username);
			return Collections.emptyMap();
		}
		return toBetMap(allUserBets);
	}

	private Map<Long, Bet> toBetMap(List<Bet> allUserBets) {
		Map<Long, Bet> matchIdBetMap = new HashMap<>();
		for (Bet bet : allUserBets) {
			if (bet.getMatch() == null) {
				LOG.error("No referenced match found for bet={}", bet);
				continue;
			}
			matchIdBetMap.put(bet.getMatch().getId(), bet);
		}
		return matchIdBetMap;
	}

	public void deleteAllMatches() {
		matchRepository.deleteAll();
	}

	public void deleteMatch(Long matchId) {
		matchRepository.delete(matchId);
	}

	public Match findMatchById(Long matchId) {
		return matchRepository.findOne(matchId);
	}

	public void save(MatchResultCommand matchResultCommand) {
		Match match = findMatchById(matchResultCommand.getMatchId());
		match.setGoalsTeamOne(matchResultCommand.getTeamResultOne());
		match.setGoalsTeamTwo(matchResultCommand.getTeamResultTwo());
		match.setPenaltyWinnerOne(matchResultCommand.isPenaltyWinnerOne());
		save(match);
	}

}

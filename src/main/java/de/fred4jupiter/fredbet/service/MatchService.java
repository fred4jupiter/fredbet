package de.fred4jupiter.fredbet.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.domain.MatchBuilder;
import de.fred4jupiter.fredbet.domain.Team;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.repository.MatchRepository;
import de.fred4jupiter.fredbet.repository.TeamRepository;
import de.fred4jupiter.fredbet.util.DateUtils;
import de.fred4jupiter.fredbet.web.matches.MatchCommand;

@Service
public class MatchService {

	private static final Logger LOG = LoggerFactory.getLogger(MatchService.class);

	@Autowired
	private MatchRepository matchRepository;

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private PointsCalculationService pointsCalculationService;

	@Autowired
	private BettingService bettingService;
	
	@Autowired
	private BetRepository betRepository;

	public List<Match> findAll() {
		return matchRepository.findAll();
	}

	public MatchCommand findByMatchId(String matchId) {
		Assert.notNull(matchId);
		Match match = matchRepository.findOne(matchId);
		Long numberOfBetsForThisMatch = betRepository.countByMatch(match);
		MatchCommand matchCommand = toMatchCommand(match);
		if (numberOfBetsForThisMatch == 0) {
			matchCommand.setDeletable(true);
		}
		return matchCommand;
	}

	public Match findMatchByMatchId(String matchId) {
		return matchRepository.findOne(matchId);
	}

	private MatchCommand toMatchCommand(Match match) {
		Assert.notNull(match);
		MatchCommand matchCommand = new MatchCommand();
		matchCommand.setMatchId(match.getId());
		matchCommand.setTeamNameOne(match.getTeamOne().getName());
		matchCommand.setTeamNameTwo(match.getTeamTwo().getName());
		matchCommand.setTeamResultOne(match.getGoalsTeamOne());
		matchCommand.setTeamResultTwo(match.getGoalsTeamTwo());
		matchCommand.setKickOffDate(DateUtils.toLocalDateTime(match.getKickOffDate()));
		matchCommand.setStadium(match.getStadium());
		matchCommand.setGroup(match.getGroup());
		return matchCommand;
	}

	public Match save(Match match) {
		Team teamOne = findOrCreate(match.getTeamOne().getName());
		Team teamTwo = findOrCreate(match.getTeamTwo().getName());

		match.setTeamOne(teamOne);
		match.setTeamTwo(teamTwo);

		match = saveMatch(match);
		return match;
	}

	private Match saveMatch(Match match) {
		matchRepository.save(match);
		
		if (match.hasResultSet()) {
			pointsCalculationService.calculatePointsFor(match);
		}
		
		return match;
	}

	private Team findOrCreate(String teamName) {
		Team team = teamRepository.findByName(teamName);
		if (team == null) {
			team = new Team(teamName);
		}

		return teamRepository.save(team);
	}

	public String save(MatchCommand matchCommand) {
		Match match = matchRepository.findOne(matchCommand.getMatchId());
		if (match == null) {
			match = MatchBuilder.create().withTeams(matchCommand.getTeamNameOne(), matchCommand.getTeamNameTwo()).build();
		} else {
			match.getTeamOne().setName(matchCommand.getTeamNameOne());
			match.getTeamTwo().setName(matchCommand.getTeamNameTwo());
		}

		toMatch(matchCommand, match);

		match = saveMatch(match);
		matchCommand.setMatchId(match.getId());

		return match.getId();
	}

	private void toMatch(MatchCommand matchCommand, Match match) {
		match.setGoalsTeamOne(matchCommand.getTeamResultOne());
		match.setGoalsTeamTwo(matchCommand.getTeamResultTwo());
		match.setKickOffDate(DateUtils.toDate(matchCommand.getKickOffDate()));
		match.setGroup(matchCommand.getGroup());
		match.setStadium(matchCommand.getStadium());
	}

	public List<MatchCommand> findAllMatches(String username) {
		List<Match> allMatches = matchRepository.findAllByOrderByKickOffDateAsc();
		return toMatchCommandsWithBets(username, allMatches);
	}
	
	public List<MatchCommand> findAllMatchesBeginAfterNow(String username) {
		List<Match> allMatches = matchRepository.findByKickOffDateGreaterThanOrderByKickOffDateAsc(new Date());
		return toMatchCommandsWithBets(username, allMatches);
	}

	private List<MatchCommand> toMatchCommandsWithBets(String username, List<Match> allMatches) {
		final Map<String, Bet> matchToBetMap = findBetsForMatchIds(username);
		final List<MatchCommand> resultList = new ArrayList<>();
		for (Match match : allMatches) {
			MatchCommand matchCommand = toMatchCommand(match);
			Bet bet = matchToBetMap.get(match.getId());
			if (bet != null) {
				matchCommand.setUserBetGoalsTeamOne(bet.getGoalsTeamOne());
				matchCommand.setUserBetGoalsTeamTwo(bet.getGoalsTeamTwo());
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

	private Map<String, Bet> findBetsForMatchIds(String username) {
		List<Bet> allUserBets = bettingService.findAllByUsername(username);
		if (CollectionUtils.isEmpty(allUserBets)) {
			LOG.debug("Could not found any bets for user: {}", username);
			return Collections.emptyMap();
		}
		return toBetMap(allUserBets);
	}

	private Map<String, Bet> toBetMap(List<Bet> allUserBets) {
		Map<String, Bet> matchIdBetMap = new HashMap<>();
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

	public void deleteMatch(String matchId) {
		matchRepository.delete(matchId);
	}

}

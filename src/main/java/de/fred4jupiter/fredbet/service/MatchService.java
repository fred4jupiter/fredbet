package de.fred4jupiter.fredbet.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.domain.MatchBuilder;
import de.fred4jupiter.fredbet.domain.Team;
import de.fred4jupiter.fredbet.repository.MatchRepository;
import de.fred4jupiter.fredbet.repository.TeamRepository;
import de.fred4jupiter.fredbet.web.matches.MatchCommand;

@Service
public class MatchService {

	@Autowired
	private MatchRepository matchRepository;

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private PointsCalculationService pointsCalculationService;
	
	@Autowired
	private BettingService bettingService;

	public List<Match> findAll() {
		return matchRepository.findAll();
	}

	public MatchCommand findByMatchId(String matchId) {
		Assert.notNull(matchId);
		Match match = matchRepository.findOne(matchId);
		return toMatchCommand(match);
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
		matchCommand.setKickOffDate(match.getKickOffDate());
		matchCommand.setStadium(match.getStadium());
		matchCommand.setGroup(match.getGroup());
		return matchCommand;
	}

	public Match save(Match match) {
		Team teamOne = findOrCreate(match.getTeamOne().getName());
		Team teamTwo = findOrCreate(match.getTeamTwo().getName());

		match.setTeamOne(teamOne);
		match.setTeamTwo(teamTwo);

		match = matchRepository.save(match);
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

		match = this.matchRepository.save(match);
		matchCommand.setMatchId(match.getId());

		if (match.hasResultSet()) {
			pointsCalculationService.calculatePointsFor(match);
		}

		return match.getId();
	}

	private void toMatch(MatchCommand matchCommand, Match match) {
		match.setGoalsTeamOne(matchCommand.getTeamResultOne());
		match.setGoalsTeamTwo(matchCommand.getTeamResultTwo());
		match.setKickOffDate(matchCommand.getKickOffDate());
		match.setGroup(matchCommand.getGroup());
		match.setStadium(matchCommand.getStadium());
	}

	public List<MatchCommand> findAllMatches(String username) {
		List<Match> allMatches = matchRepository.findAllByOrderByKickOffDateAsc();
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

	private Map<String, Bet> findBetsForMatchIds(String username) {
		List<Bet> allUserBets = bettingService.findAllByUsername(username);
		Map<String,Bet> matchToBetMap = toBetMap(allUserBets);
		return matchToBetMap;
	}

	private Map<String, Bet> toBetMap(List<Bet> allUserBets) {
		Map<String, Bet> matchIdBetMap = new HashMap<>();
		for (Bet bet : allUserBets) {
			matchIdBetMap.put(bet.getMatch().getId(), bet);
		}
		return matchIdBetMap;
	}

	public List<MatchCommand> findMatchesByGroup(String currentUserName, Group group) {
		// TODO Auto-generated method stub
		return null;
	}

}

package de.fred4jupiter.fredbet.web.matches;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.service.BettingService;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.util.DateUtils;
import de.fred4jupiter.fredbet.util.Validator;
import de.fred4jupiter.fredbet.web.WebMessageUtil;

@Component
public class MatchCommandMapper {

	private static final Logger LOG = LoggerFactory.getLogger(MatchCommandMapper.class);

	@Autowired
	private BettingService bettingService;

	@Autowired
	private MatchService matchService;

	@Autowired
	private WebMessageUtil messageUtil;

	public List<MatchCommand> findAllMatches(String username) {
		List<Match> allMatches = matchService.findAllMatches();
		return toMatchCommandsWithBets(username, allMatches);
	}

	public List<MatchCommand> findAllUpcomingMatches(String username) {
		List<Match> allMatches = matchService.findUpcomingMatches();
		return toMatchCommandsWithBets(username, allMatches);
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

	private List<MatchCommand> toMatchCommandsWithBets(String username, List<Match> allMatches) {
		final Map<Long, Bet> matchToBetMap = findBetsForMatchIds(username);
		final List<MatchCommand> resultList = new ArrayList<>();
		for (Match match : allMatches) {
			MatchCommand matchCommand = toMatchCommand(match);
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
		List<Match> allMatches = matchService.findMatchesByGroup(group);
		return toMatchCommandsWithBets(currentUserName, allMatches);
	}

	public MatchCommand toMatchCommand(Match match) {
		Assert.notNull(match, "Match must be given");
		MatchCommand matchCommand = new MatchCommand(messageUtil);
		matchCommand.setMatchId(match.getId());
		matchCommand.setCountryTeamOne(match.getCountryOne());
		matchCommand.setCountryTeamTwo(match.getCountryTwo());
		matchCommand.setNameTeamOne(match.getTeamNameOne());
		matchCommand.setNameTeamTwo(match.getTeamNameTwo());
		matchCommand.setTeamResultOne(match.getGoalsTeamOne());
		matchCommand.setTeamResultTwo(match.getGoalsTeamTwo());
		matchCommand.setKickOffDate(DateUtils.toLocalDateTime(match.getKickOffDate()));
		matchCommand.setStadium(match.getStadium());
		matchCommand.setGroup(match.getGroup());
		matchCommand.setPenaltyWinnerOneMatch(match.isPenaltyWinnerOne());
		return matchCommand;
	}

}

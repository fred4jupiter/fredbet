package de.fred4jupiter.fredbet.web.bet;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.service.BettingService;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.util.DateUtils;
import de.fred4jupiter.fredbet.web.WebMessageUtil;

@Component
public class AllBetsCommandMapper {

	private static final Logger LOG = LoggerFactory.getLogger(AllBetsCommandMapper.class);

	@Autowired
	private MatchService matchService;

	@Autowired
	private BettingService bettingService;

	@Autowired
	private WebMessageUtil webMessageUtil;

	public AllBetsCommand findAllBetsForMatchId(final Long matchId) {
		if (matchId == null) {
			return null;
		}
		Match match = matchService.findMatchById(matchId);
		if (match == null) {
			LOG.warn("Match with matchId={} could not be found!", matchId);
			return null;
		}

		List<Bet> filtered = bettingService.findAllBetsForMatchId(matchId);
		return toAllBetsCommand(match, filtered);

	}

	private AllBetsCommand toAllBetsCommand(Match match, List<Bet> filtered) {
		AllBetsCommand allBetsCommand = new AllBetsCommand();
		allBetsCommand.setAllBetsForMatch(filtered);
		allBetsCommand.setMatch(match);

		allBetsCommand.setTeamNameOne(webMessageUtil.getTeamNameOne(match));
		allBetsCommand.setTeamNameTwo(webMessageUtil.getTeamNameTwo(match));

		allBetsCommand.setCountryTeamOne(match.getCountryOne());
		allBetsCommand.setCountryTeamTwo(match.getCountryTwo());
		allBetsCommand.setKickOffDate(DateUtils.toLocalDateTime(match.getKickOffDate()));
		allBetsCommand.setStadium(match.getStadium());
		allBetsCommand.setGroup(match.getGroup());
		return allBetsCommand;
	}

	

}

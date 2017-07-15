package de.fred4jupiter.fredbet.web.bet;

import java.util.List;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.util.DateUtils;
import de.fred4jupiter.fredbet.web.AbstractMatchHeaderCommand;
import de.fred4jupiter.fredbet.web.WebMessageUtil;

public class AllBetsCommand extends AbstractMatchHeaderCommand {

	private final List<Bet> allBetsForMatch;

	private final Match match;

	public AllBetsCommand(List<Bet> allBetsForMatch, Match match, WebMessageUtil messageUtil) {
		super(messageUtil);
		this.allBetsForMatch = allBetsForMatch;
		this.match = match;
		setCountryTeamOne(match.getCountryOne());
		setCountryTeamTwo(match.getCountryTwo());
		setNameTeamOne(match.getTeamNameOne());
		setNameTeamTwo(match.getTeamNameTwo());
		setKickOffDate(DateUtils.toLocalDateTime(match.getKickOffDate()));
		setStadium(match.getStadium());
		setGroup(match.getGroup());
	}

	public List<Bet> getAllBetsForMatch() {
		return allBetsForMatch;
	}

	public Long getMatchId() {
		return this.match.getId();
	}
	
	public Match getMatch() {
		return match;
	}
}

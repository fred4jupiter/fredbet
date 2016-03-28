package de.fred4jupiter.fredbet.web.bet;

import java.util.List;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.util.DateUtils;
import de.fred4jupiter.fredbet.web.AbstractMatchHeaderCommand;
import de.fred4jupiter.fredbet.web.MessageUtil;

public class AllBetsCommand extends AbstractMatchHeaderCommand {

	private final List<Bet> allBetsForMatch;

	public AllBetsCommand(List<Bet> allBetsForMatch, Match match, MessageUtil messageUtil) {
		super(messageUtil);
		this.allBetsForMatch = allBetsForMatch;
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

}

package de.fred4jupiter.fredbet.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.util.DateUtils;
import de.fred4jupiter.fredbet.web.matches.MatchCommand;

@Component
public class MatchConverter {

	@Autowired
	private MessageUtil messageUtil;
	
	public MatchCommand toMatchCommand(Match match) {
		Assert.notNull(match);
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
	
	public void toMatch(MatchCommand matchCommand, Match match) {
		match.setCountryOne(matchCommand.getCountryTeamOne());
		match.setCountryTwo(matchCommand.getCountryTeamTwo());
		match.setTeamNameOne(matchCommand.getNameTeamOne());
		match.setTeamNameTwo(matchCommand.getNameTeamTwo());
		match.setGoalsTeamOne(matchCommand.getTeamResultOne());
		match.setGoalsTeamTwo(matchCommand.getTeamResultTwo());
		match.setKickOffDate(DateUtils.toDate(matchCommand.getKickOffDate()));
		match.setGroup(matchCommand.getGroup());
		match.setStadium(matchCommand.getStadium());
	}
}

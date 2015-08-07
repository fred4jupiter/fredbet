package de.fred4jupiter.fredbet.domain;

import java.time.LocalDateTime;

import de.fred4jupiter.fredbet.util.DateUtils;

public class MatchBuilder {

	private final Match match;
	
	private MatchBuilder() {
		match = new Match();
	}
	
	public static MatchBuilder create() {
		return new MatchBuilder();
	}
	
	public MatchBuilder withTeams(String teamNameOne, String teamNameTwo) {
		Team teamOne = new Team(teamNameOne);
		Team teamTwo = new Team(teamNameTwo);
		match.setTeamOne(teamOne);
		match.setTeamTwo(teamTwo);
		return this;
	}
	
	public MatchBuilder withGoals(Integer goalsTeamOne, Integer goalsTeamTwo) {
		match.setGoalsTeamOne(goalsTeamOne);
		match.setGoalsTeamTwo(goalsTeamTwo);
		return this;
	}
	
	public MatchBuilder withKickOffDateAndStatium(LocalDateTime kickOffDate, String stadium) {
		match.setKickOffDate(DateUtils.toDate(kickOffDate));
		match.setStadium(stadium);
		return this;
	}
	
	public MatchBuilder withGroup(String group) {
		match.setGroup(group);
		return this;
	}
	
	public Match build() {
		return match;
	}
}

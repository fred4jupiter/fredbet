package de.fred4jupiter.fredbet.domain;

import java.time.LocalDateTime;

import de.fred4jupiter.fredbet.util.DateUtils;

public class MatchBuilder {

	private final Match match;

	private MatchBuilder() {
		match = new Match();
		match.setKickOffDate(DateUtils.toDate(LocalDateTime.now()));
	}

	public static MatchBuilder create() {
		return new MatchBuilder();
	}

	public MatchBuilder withTeams(String teamOne, String teamTwo) {
		match.setTeamOne(new Team(teamOne));
		match.setTeamTwo(new Team(teamTwo));
		return this;
	}

	public MatchBuilder withTeams(Team teamOne, Team teamTwo) {
		match.setTeamOne(teamOne);
		match.setTeamTwo(teamTwo);
		return this;
	}

	public MatchBuilder withTeams(Country one, Country two) {
		match.setTeamOne(new Team(one));
		match.setTeamTwo(new Team(two));
		return this;
	}

	public MatchBuilder withGoals(Integer goalsTeamOne, Integer goalsTeamTwo) {
		match.setGoalsTeamOne(goalsTeamOne);
		match.setGoalsTeamTwo(goalsTeamTwo);
		return this;
	}

	public MatchBuilder withKickOffDate(LocalDateTime kickOffDate) {
		match.setKickOffDate(DateUtils.toDate(kickOffDate));
		return this;
	}

	public MatchBuilder withKickOffDate(int day, int month, int hour) {
		match.setKickOffDate(DateUtils.toDate(LocalDateTime.of(2016, month, day, hour, 0)));
		return this;
	}

	public MatchBuilder withGroup(Group group) {
		match.setGroup(group);
		return this;
	}

	public MatchBuilder withStadium(String stadium) {
		match.setStadium(stadium);
		return this;
	}

	public Match build() {
		return match;
	}
}

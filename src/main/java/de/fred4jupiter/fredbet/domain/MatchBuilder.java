package de.fred4jupiter.fredbet.domain;

import java.time.LocalDateTime;

public class MatchBuilder {

	private final Match match;

	private MatchBuilder() {
		match = new Match();
		match.setKickOffDate(LocalDateTime.now());
	}

	public static MatchBuilder create() {
		return new MatchBuilder();
	}

	public MatchBuilder withTeams(String teamOne, String teamTwo) {
		match.setTeamNameOne(teamOne);
		match.setTeamNameTwo(teamTwo);
		return this;
	}

	public MatchBuilder withTeams(Country one, Country two) {
		match.setCountryOne(one);
		match.setCountryTwo(two);
		return this;
	}

	public MatchBuilder withGoals(Integer goalsTeamOne, Integer goalsTeamTwo) {
		match.setGoalsTeamOne(goalsTeamOne);
		match.setGoalsTeamTwo(goalsTeamTwo);
		return this;
	}

	public MatchBuilder withKickOffDate(LocalDateTime kickOffDate) {
		match.setKickOffDate(kickOffDate);
		return this;
	}

	public MatchBuilder withKickOffDate(int day, int month, int hour) {
		match.setKickOffDate(LocalDateTime.of(LocalDateTime.now().getYear(), month, day, hour, 0));
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

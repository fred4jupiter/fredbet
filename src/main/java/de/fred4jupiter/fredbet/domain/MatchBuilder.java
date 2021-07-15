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
        match.getTeamOne().setName(teamOne);
        match.getTeamTwo().setName(teamTwo);
        return this;
    }

    public MatchBuilder withTeams(Country one, Country two) {
        match.getTeamOne().setCountry(one);
        match.getTeamTwo().setCountry(two);
        return this;
    }

    public MatchBuilder withTeams(Team one, Team two) {
        match.setTeamOne(one);
        match.setTeamTwo(two);
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

    public MatchBuilder withPenaltyWinnerOne(boolean penaltyWinnerOne) {
        match.setPenaltyWinnerOne(penaltyWinnerOne);
        return this;
    }

    public Match build() {
        return match;
    }
}

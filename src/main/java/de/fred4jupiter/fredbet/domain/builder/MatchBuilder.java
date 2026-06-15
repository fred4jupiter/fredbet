package de.fred4jupiter.fredbet.domain.builder;

import de.fred4jupiter.fredbet.team.TeamService;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.domain.entity.Team;

import java.time.LocalDateTime;

public class MatchBuilder {

    private final Match match;

    private final TeamService teamService;

    private MatchBuilder(TeamService teamService) {
        this.teamService = teamService;
        match = new Match();
        match.setKickOffDate(LocalDateTime.now());
    }

    public static MatchBuilder create(TeamService teamService) {
        return new MatchBuilder(teamService);
    }

    public MatchBuilder withTeamOne(String teamOne) {
        Country country = Country.fromName(teamOne);
        if (country != null) {
            match.getTeamOne().setCountry(country);
        } else {
            match.getTeamOne().setName(teamOne);
        }

        return this;
    }

    public MatchBuilder withTeamTwo(String teamTwo) {
        Country country = Country.fromName(teamTwo);
        if (country != null) {
            match.getTeamTwo().setCountry(country);
        } else {
            match.getTeamTwo().setName(teamTwo);
        }

        return this;
    }

    public MatchBuilder withTeams(String teamOne, String teamTwo) {
        withTeamOne(teamOne);
        withTeamTwo(teamTwo);
        return this;
    }

    public MatchBuilder withTeams(Country one, Country two) {
        match.getTeamOne().setCountry(one);
        match.getTeamTwo().setCountry(two);
        return this;
    }

    public MatchBuilder withTeams(Team one, Team two) {
        Team newTeamOne = new Team();
        newTeamOne.setName(one.getName());
        newTeamOne.setCountry(one.getCountry());
        newTeamOne.setSvgContent(one.getSvgContent());

        Team newTeamTwo = new Team();
        newTeamTwo.setName(two.getName());
        newTeamTwo.setCountry(two.getCountry());
        newTeamTwo.setSvgContent(two.getSvgContent());

        match.setTeamOne(newTeamOne);
        match.setTeamTwo(newTeamTwo);
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
        // resolve to existing teams or create new ones
        Team teamOne = findOrCreateTeamFor(match.getTeamOne());
        if (teamOne != null) {
            match.setTeamOne(teamOne);
        }

        Team teamTwo = findOrCreateTeamFor(match.getTeamTwo());
        if (teamTwo != null) {
            match.setTeamTwo(teamTwo);
        }

        return match;
    }

    private Team findOrCreateTeamFor(Team team) {
        return teamService.findOrCreateTeam(team.getCountry(), team.getName());
    }
}

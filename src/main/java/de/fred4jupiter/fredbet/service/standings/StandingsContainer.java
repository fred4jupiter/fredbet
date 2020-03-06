package de.fred4jupiter.fredbet.service.standings;

import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.MatchResult;
import de.fred4jupiter.fredbet.domain.Team;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class StandingsContainer {

    private final Map<Group, List<TeamStandings>> standingsMap = new HashMap<>();

    private final MessageSourceUtil messageSourceUtil;

    public StandingsContainer(MessageSourceUtil messageSourceUtil) {
        this.messageSourceUtil = messageSourceUtil;
    }

    public void registerResult(MatchResult matchResult, Locale locale) {
        TeamStandings teamPointsTeamOne = getGroupTeamPointsByGroupAndName(matchResult.getGroup(), getTranslatedTeamName(matchResult.getTeamOne(), locale));
        teamPointsTeamOne.registerResultForTeam(matchResult.getTeamOne(), matchResult.getGoalDifference(), matchResult.getTeamTwo(), matchResult.isTeamOneWinner(), matchResult.isUndecidedResult());

        TeamStandings teamPointsTeamTwo = getGroupTeamPointsByGroupAndName(matchResult.getGroup(), getTranslatedTeamName(matchResult.getTeamTwo(), locale));
        teamPointsTeamTwo.registerResultForTeam(matchResult.getTeamTwo(), matchResult.getGoalDifference(), matchResult.getTeamOne(), matchResult.isTeamTwoWinner(), matchResult.isUndecidedResult());
    }

    private String getTranslatedTeamName(Team team, Locale locale) {
        if (StringUtils.isNotBlank(team.getName())) {
            return team.getName();
        }
        return messageSourceUtil.getCountryName(team.getCountry(), locale);
    }

    private TeamStandings getGroupTeamPointsByGroupAndName(Group group, String teamName) {
        List<TeamStandings> list = standingsMap.computeIfAbsent(group, k -> new ArrayList<>());
        return getOrCreate(list, teamName);
    }

    private TeamStandings getOrCreate(List<TeamStandings> list, String teamName) {
        for (TeamStandings goupTeamPoints : list) {
            if (goupTeamPoints.getTeamName().equals(teamName)) {
                return goupTeamPoints;
            }
        }

        TeamStandings teamStandings = new TeamStandings(teamName);
        list.add(teamStandings);
        return teamStandings;
    }

    public List<Group> getGroups() {
        return Group.getMainGroups();
    }

    public List<TeamStandings> getForGroup(Group group) {
        List<TeamStandings> teamPoints = standingsMap.get(group);
        if (teamPoints == null || teamPoints.isEmpty()) {
            return Collections.emptyList();
        }

        Comparator<TeamStandings> comparator1 = Comparator.comparingInt(TeamStandings::getNumberOfPoints).reversed();
        Comparator<TeamStandings> comparator2 = Comparator.comparingInt(TeamStandings::getNumberOfGoalDifference);

        return teamPoints.stream().sorted(comparator1.thenComparing(comparator2)).collect(Collectors.toList());
    }
}

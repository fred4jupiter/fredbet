package de.fred4jupiter.fredbet.standings;

import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class StandingsContainer {

    private final Map<Group, List<TeamStandings>> standingsMap = new HashMap<>();

    private final MessageSourceUtil messageSourceUtil;

    public StandingsContainer(MessageSourceUtil messageSourceUtil) {
        this.messageSourceUtil = messageSourceUtil;
    }

    public void registerResult(Match match, Locale locale) {
        TeamStandings teamPointsTeamOne = getGroupTeamPointsByGroupAndName(match.getGroup(), match.getTeamOne());
        teamPointsTeamOne.registerResultForTeam(match);

        TeamStandings teamPointsTeamTwo = getGroupTeamPointsByGroupAndName(match.getGroup(), match.getTeamTwo());
        teamPointsTeamTwo.registerResultForTeam(match);
    }

    private String getTranslatedTeamName(Team team, Locale locale) {
        if (StringUtils.isNotBlank(team.getName())) {
            return team.getName();
        }
        return messageSourceUtil.getCountryName(team.getCountry(), locale);
    }

    private TeamStandings getGroupTeamPointsByGroupAndName(Group group, Team team) {
        List<TeamStandings> list = standingsMap.computeIfAbsent(group, k -> new ArrayList<>());
        return getOrCreate(list, team);
    }

    private TeamStandings getOrCreate(List<TeamStandings> list, Team team) {
        for (TeamStandings goupTeamPoints : list) {
            if (goupTeamPoints.getTeam().getId().equals(team.getId())) {
                return goupTeamPoints;
            }
        }

        TeamStandings teamStandings = new TeamStandings(team);
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

        Comparator<TeamStandings> points = Comparator.comparingInt(TeamStandings::getNumberOfPoints).reversed();
        Comparator<TeamStandings> goalDifference = Comparator.comparingInt(TeamStandings::getNumberOfGoalDifference).reversed();
        Comparator<TeamStandings> goals = Comparator.comparingInt(TeamStandings::getNumberOfGoals).reversed();
        Comparator<TeamStandings> goalsAgainst = Comparator.comparingInt(TeamStandings::getNumberOfGoalsAgainst);

        Comparator<TeamStandings> completeComparator = points.thenComparing(goalDifference)
            .thenComparing(goals).thenComparing(goalsAgainst);
        return teamPoints.stream().sorted(completeComparator).toList();
    }
}

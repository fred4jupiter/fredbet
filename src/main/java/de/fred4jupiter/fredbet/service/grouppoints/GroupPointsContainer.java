package de.fred4jupiter.fredbet.service.grouppoints;

import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.MatchResult;
import de.fred4jupiter.fredbet.domain.Team;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class GroupPointsContainer {

    private final Map<Group, List<GroupTeamPoints>> groupWithTeamsMap = new HashMap<>();

    private final MessageSourceUtil messageSourceUtil;

    public GroupPointsContainer(MessageSourceUtil messageSourceUtil) {
        this.messageSourceUtil = messageSourceUtil;
    }

    public void registerResult(MatchResult matchResult, Locale locale) {
        GroupTeamPoints groupTeamPointsTeamOne = getGroupTeamPointsByGroupAndName(matchResult.getGroup(), getTranslatedTeamName(matchResult.getTeamOne(), locale));
        groupTeamPointsTeamOne.registerResultForTeam(matchResult.getTeamOne(), matchResult.getGoalDifference(), matchResult.getTeamTwo(), matchResult.isTeamOneWinner(), matchResult.isUndecidedResult());

        GroupTeamPoints groupTeamPointsTeamTwo = getGroupTeamPointsByGroupAndName(matchResult.getGroup(), getTranslatedTeamName(matchResult.getTeamTwo(), locale));
        groupTeamPointsTeamTwo.registerResultForTeam(matchResult.getTeamTwo(), matchResult.getGoalDifference(), matchResult.getTeamOne(), matchResult.isTeamTwoWinner(), matchResult.isUndecidedResult());
    }

    private String getTranslatedTeamName(Team team, Locale locale) {
        if (StringUtils.isNotBlank(team.getName())) {
            return team.getName();
        }
        return messageSourceUtil.getCountryName(team.getCountry(), locale);
    }

    private GroupTeamPoints getGroupTeamPointsByGroupAndName(Group group, String teamName) {
        List<GroupTeamPoints> list = groupWithTeamsMap.computeIfAbsent(group, k -> new ArrayList<>());
        return getOrCreate(list, teamName);
    }

    private GroupTeamPoints getOrCreate(List<GroupTeamPoints> list, String teamName) {
        for (GroupTeamPoints goupTeamPoints : list) {
            if (goupTeamPoints.getTeamName().equals(teamName)) {
                return goupTeamPoints;
            }
        }

        GroupTeamPoints groupTeamPoints = new GroupTeamPoints(teamName);
        list.add(groupTeamPoints);
        return groupTeamPoints;
    }

    public List<Group> getGroups() {
        return Group.getMainGroups();
    }

    public List<GroupTeamPoints> getForGroup(Group group) {
        List<GroupTeamPoints> groupTeamPoints = groupWithTeamsMap.get(group);
        if (groupTeamPoints == null || groupTeamPoints.isEmpty()) {
            return Collections.emptyList();
        }

        Comparator<GroupTeamPoints> comparator1 = Comparator.comparingInt(GroupTeamPoints::getNumberOfPoints).reversed();
        Comparator<GroupTeamPoints> comparator2 = Comparator.comparingInt(GroupTeamPoints::getNumberOfGoalDifference);

        return groupTeamPoints.stream().sorted(comparator1.thenComparing(comparator2)).collect(Collectors.toList());
    }
}

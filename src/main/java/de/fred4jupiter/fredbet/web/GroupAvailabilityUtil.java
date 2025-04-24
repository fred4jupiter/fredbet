package de.fred4jupiter.fredbet.web;

import de.fred4jupiter.fredbet.betting.BettingService;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.match.MatchService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Component
public class GroupAvailabilityUtil {

    private final MatchService matchService;

    private final BettingService bettingService;

    public GroupAvailabilityUtil(MatchService matchService, BettingService bettingService) {
        this.matchService = matchService;
        this.bettingService = bettingService;
    }

    public boolean isGroupAvailable(String groupName) {
        return isGroupAvailable(Group.valueOf(groupName));
    }

    public boolean isGroupAvailable(Group group) {
        return matchService.availableGroups().contains(group);
    }

    public boolean isKnockOutMatchesAvailable() {
        return matchService.isKnockOutMatchesAvailable();
    }

    public boolean hasUserBetsWithJoker() {
        return bettingService.hasUserBetsWithJoker();
    }

    public List<Group> getMainGroups() {
        Set<Group> groups = matchService.availableGroups();
        return groups.stream().filter(Group::isMainGroup).toList();
    }
}

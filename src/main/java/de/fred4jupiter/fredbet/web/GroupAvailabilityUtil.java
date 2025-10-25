package de.fred4jupiter.fredbet.web;

import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.match.MatchService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class GroupAvailabilityUtil {

    private final MatchService matchService;

    public GroupAvailabilityUtil(MatchService matchService) {
        this.matchService = matchService;
    }

    public boolean isGroupAvailable(String groupName) {
        return isGroupAvailable(Group.valueOf(groupName));
    }

    private boolean isGroupAvailable(Group group) {
        return matchService.availableGroups().contains(group);
    }

    public boolean isKnockOutMatchesAvailable() {
        return matchService.isKnockOutMatchesAvailable();
    }

    public List<Group> getMainGroups() {
        Set<Group> groups = matchService.availableGroups();
        return groups.stream().filter(Group::isMainGroup).sorted().toList();
    }
}

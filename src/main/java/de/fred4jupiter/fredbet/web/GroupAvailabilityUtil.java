package de.fred4jupiter.fredbet.web;

import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.match.MatchService;
import org.springframework.stereotype.Component;

import java.util.*;

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

    public List<Group> getGroups() {
        Set<Group> groups = matchService.availableGroups();
        if (groups.isEmpty()) {
            return Collections.emptyList();
        }
        return groups.stream().filter(Objects::nonNull).sorted(Comparator.comparing(Group::ordinal)).toList();
    }

    public boolean isDividerNecessary(Group group) {
        if (group == null) {
            return false;
        }

        final Set<Group> groups = matchService.availableGroups();
        if (groups.contains(Group.ROUND_OF_THIRTY_TWO) && Group.ROUND_OF_THIRTY_TWO.equals(group)) {
            return true;
        } else if (Group.ROUND_OF_SIXTEEN.equals(group)) {
            return true;
        }

        return List.of(Group.FINAL, Group.GAME_FOR_THIRD, Group.GROUP_A).contains(group);
    }
}

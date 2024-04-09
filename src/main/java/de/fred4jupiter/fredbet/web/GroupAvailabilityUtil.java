package de.fred4jupiter.fredbet.web;

import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.service.BettingService;
import de.fred4jupiter.fredbet.service.MatchService;
import org.springframework.stereotype.Component;

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
}

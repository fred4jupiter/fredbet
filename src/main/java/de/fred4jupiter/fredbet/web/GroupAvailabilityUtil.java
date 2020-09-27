package de.fred4jupiter.fredbet.web;

import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.RuntimeSettings;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.service.config.RuntimeSettingsService;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class GroupAvailabilityUtil {

    private final MatchService matchService;

    private final RuntimeSettingsService runtimeSettingsService;

    public GroupAvailabilityUtil(MatchService matchService, RuntimeSettingsService runtimeSettingsService) {
        this.matchService = matchService;
        this.runtimeSettingsService = runtimeSettingsService;
    }

    public boolean isGroupAvailable(String groupName) {
        return isGroupAvailable(Group.valueOf(groupName));
    }

    public boolean isGroupAvailable(Group group) {
        return matchService.availableGroups().contains(group);
    }

    public boolean isKnockOutMatchesAvailable() {
        Set<Group> availableGroups = matchService.availableGroups();
        for (Group group : availableGroups) {
            if (!group.getName().startsWith("GROUP")) {
                return true;
            }
        }

        return false;
    }

    public boolean isJokerBettingActive() {
        RuntimeSettings runtimeSettings = runtimeSettingsService.loadRuntimeSettings();
        return runtimeSettings.getJokerMaxCount() > 0;
    }
}

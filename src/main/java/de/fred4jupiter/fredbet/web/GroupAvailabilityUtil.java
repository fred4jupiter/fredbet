package de.fred4jupiter.fredbet.web;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.RuntimeConfig;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.service.config.RuntimeConfigurationService;

@Component
public class GroupAvailabilityUtil {

	@Autowired
	private MatchService matchService;
	
	@Autowired
	private RuntimeConfigurationService runtimeConfigurationService;
	
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
		RuntimeConfig runtimeConfig = runtimeConfigurationService.loadRuntimeConfig();
		return runtimeConfig.getJokerMaxCount() > 0;
	}
}

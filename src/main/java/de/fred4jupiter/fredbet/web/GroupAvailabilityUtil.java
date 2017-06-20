package de.fred4jupiter.fredbet.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.service.MatchService;

@Component
public class GroupAvailabilityUtil {

	@Autowired
	private MatchService matchService;
	
	public boolean isGroupAvailable(String groupName) {
		return isGroupAvailable(Group.valueOf(groupName));
	}
	
	public boolean isGroupAvailable(Group group) {
		return matchService.availableGroups().contains(group);
	}

}

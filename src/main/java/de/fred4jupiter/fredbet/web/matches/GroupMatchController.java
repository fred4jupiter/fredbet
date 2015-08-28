package de.fred4jupiter.fredbet.web.matches;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.web.SecurityBean;

@Controller
@RequestMapping("/groups")
public class GroupMatchController {

	@Autowired
	private MatchService matchService;

	@Autowired
	private SecurityBean securityBean;

	@ModelAttribute("groups")
	public List<Group> availableGroups() {
		List<Group> groups = Arrays.asList(Group.values());
		return groups.stream().filter(group -> group.name().startsWith("GROUP_")).collect(Collectors.toList());
	}

	@RequestMapping
	public ModelAndView list() {
		List<MatchCommand> matches = matchService.findAllMatches(securityBean.getCurrentUserName());

		Map<String, List<MatchCommand>> groupMatches = matches.stream().collect(Collectors.groupingBy(matchCommand -> matchCommand.getGroup().name()));

		return new ModelAndView("matches/groups", "groupMatches", groupMatches);
	}
}

package de.fred4jupiter.fredbet.web.matches;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;

@Controller
@RequestMapping("/matches")
public class MatchController {

	private static final String VIEW_LIST_MATCHES = "matches/list";

	@Autowired
	private SecurityService securityBean;

	@Autowired
	private WebMessageUtil messageUtil;

	@Autowired
	private MatchCommandMapper matchCommandMapper;

	@ModelAttribute("matchCommand")
	public MatchCommand matchCommand() {
		return new MatchCommand(messageUtil);
	}

	@RequestMapping
	public ModelAndView listAllMatches() {
		List<MatchCommand> matches = matchCommandMapper.findAllMatches(securityBean.getCurrentUserName());
		ModelAndView modelAndView = new ModelAndView(VIEW_LIST_MATCHES, "allMatches", matches);
		modelAndView.addObject("heading", messageUtil.getMessageFor("all.matches"));
		return modelAndView;
	}

	@RequestMapping(value = "upcoming")
	public ModelAndView upcomingMatches() {
		List<MatchCommand> matches = matchCommandMapper.findAllUpcomingMatches(securityBean.getCurrentUserName());
		ModelAndView modelAndView = new ModelAndView(VIEW_LIST_MATCHES, "allMatches", matches);
		modelAndView.addObject("heading", messageUtil.getMessageFor("upcoming.matches"));
		return modelAndView;
	}

	@RequestMapping(value = "/group/{groupName}")
	public ModelAndView listByGroup(@PathVariable("groupName") String groupName) {
		List<MatchCommand> matches = matchCommandMapper.findMatchesByGroup(securityBean.getCurrentUserName(), Group.valueOf(groupName));
		ModelAndView modelAndView = new ModelAndView(VIEW_LIST_MATCHES, "allMatches", matches);
		String msgKey = "group.entry." + groupName;
		modelAndView.addObject("heading", messageUtil.getMessageFor(msgKey));
		return modelAndView;
	}

}

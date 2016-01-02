package de.fred4jupiter.fredbet.web.matches;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.web.MessageUtil;
import de.fred4jupiter.fredbet.web.SecurityBean;

@Controller
@RequestMapping("/matches")
public class MatchController {

	private static final Logger LOG = LoggerFactory.getLogger(MatchController.class);

	@Autowired
	private MatchService matchService;

	@Autowired
	private SecurityBean securityBean;

	@Autowired
	private MessageUtil messageUtil;

	@ModelAttribute("availableGroups")
	public List<Group> availableGroups() {
		return Group.getAllGroups();
	}

	@RequestMapping
	public ModelAndView listAllMatches() {
		List<MatchCommand> matches = matchService.findAllMatches(securityBean.getCurrentUserName());
		ModelAndView modelAndView = new ModelAndView("matches/list", "allMatches", matches);
		modelAndView.addObject("heading", messageUtil.getMessageFor("all.matches"));
		return modelAndView;
	}

	@RequestMapping(value = "upcoming")
	public ModelAndView upcomingMatches() {
		List<MatchCommand> matches = matchService.findAllMatchesBeginAfterNow(securityBean.getCurrentUserName());
		ModelAndView modelAndView = new ModelAndView("matches/list", "allMatches", matches);
		modelAndView.addObject("heading", messageUtil.getMessageFor("upcoming.matches"));
		return modelAndView;
	}

	@RequestMapping(value = "/group/{groupName}")
	public ModelAndView listByGroup(@PathVariable("groupName") String groupName) {
		List<MatchCommand> matches = matchService.findMatchesByGroup(securityBean.getCurrentUserName(), Group.valueOf(groupName));
		ModelAndView modelAndView = new ModelAndView("matches/list", "allMatches", matches);
		String msgKey = "group.entry." + groupName;
		modelAndView.addObject("heading", messageUtil.getMessageFor(msgKey));
		return modelAndView;
	}

	@RequestMapping("{id}")
	public ModelAndView edit(@PathVariable("id") String matchId) {
		MatchCommand matchCommand = matchService.findByMatchId(matchId);
		return new ModelAndView("matches/form", "matchCommand", matchCommand);
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String createForm(@ModelAttribute MatchCommand matchCommand) {
		matchCommand.setKickOffDate(LocalDateTime.now().plusHours(1));
		return "matches/form";
	}

	@RequestMapping(value = "/delete/{matchId}", method = RequestMethod.GET)
	public ModelAndView deleteMatch(@PathVariable("matchId") String matchId, RedirectAttributes redirect) {
		LOG.debug("deleted match with id={}", matchId);

		MatchCommand matchCommand = matchService.findByMatchId(matchId);
		
		matchService.deleteMatch(matchId);
		
		messageUtil.addInfoMsg(redirect, "msg.match.deleted", matchCommand.getTeamNameOne(), matchCommand.getTeamNameTwo());
		
		return new ModelAndView("redirect:/matches");
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView createOrUpdate(@Valid MatchCommand matchCommand, BindingResult result, RedirectAttributes redirect,
			ModelMap modelMap) {
		if (result.hasErrors()) {
			return new ModelAndView("matches/form", "formErrors", result.getAllErrors());
		}

		if (validate(matchCommand, modelMap)) {
			return new ModelAndView("matches/form", "matchCommand", matchCommand);
		}

		if (StringUtils.isBlank(matchCommand.getMatchId())) {
		    messageUtil.addInfoMsg(redirect, "msg.match.created", matchCommand.getTeamNameOne(), matchCommand.getTeamNameTwo());
		}
		else {
		    messageUtil.addInfoMsg(redirect, "msg.match.updated", matchCommand.getTeamNameOne(), matchCommand.getTeamNameTwo());
		}
		
		matchService.save(matchCommand);
		return new ModelAndView("redirect:/matches");
	}

	private boolean validate(MatchCommand matchCommand, ModelMap modelMap) {
		if (matchCommand.hasValidGoals()) {
			messageUtil.addErrorMsg(modelMap, "msg.negative.values.not.allowed");
			return true;
		}

		if ((matchCommand.isOnlyOneResultSet())) {
			messageUtil.addErrorMsg(modelMap, "msg.input.complete.result");
			return true;
		}

		if (matchCommand.isDateOrTimeEmpty()) {
			messageUtil.addErrorMsg(modelMap, "msg.input.complete.date.time");
			return true;
		}

		if (matchCommand.isTeamNamesEmpty()) {
			messageUtil.addErrorMsg(modelMap, "msg.input.teamOne.teamTwo");
			return true;
		}

		if (StringUtils.isEmpty(matchCommand.getStadium())) {
			messageUtil.addErrorMsg(modelMap, "msg.input.stadium");
			return true;
		}

		return false;
	}
}

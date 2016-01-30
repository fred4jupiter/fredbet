package de.fred4jupiter.fredbet.web.matches;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.web.MessageUtil;
import de.fred4jupiter.fredbet.web.SecurityBean;

@Controller
@RequestMapping("/matches")
public class MatchController {

	private static final String MATCH_LIST_PAGE = "matches/list";

	private static final String MATCH_PAGE = "matches/form";

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
		ModelAndView modelAndView = new ModelAndView(MATCH_LIST_PAGE, "allMatches", matches);
		modelAndView.addObject("heading", messageUtil.getMessageFor("all.matches"));
		return modelAndView;
	}

	@RequestMapping(value = "upcoming")
	public ModelAndView upcomingMatches() {
		List<MatchCommand> matches = matchService.findAllMatchesBeginAfterNow(securityBean.getCurrentUserName());
		ModelAndView modelAndView = new ModelAndView(MATCH_LIST_PAGE, "allMatches", matches);
		modelAndView.addObject("heading", messageUtil.getMessageFor("upcoming.matches"));
		return modelAndView;
	}

	@RequestMapping(value = "/group/{groupName}")
	public ModelAndView listByGroup(@PathVariable("groupName") String groupName) {
		List<MatchCommand> matches = matchService.findMatchesByGroup(securityBean.getCurrentUserName(), Group.valueOf(groupName));
		ModelAndView modelAndView = new ModelAndView(MATCH_LIST_PAGE, "allMatches", matches);
		String msgKey = "group.entry." + groupName;
		modelAndView.addObject("heading", messageUtil.getMessageFor(msgKey));
		return modelAndView;
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_EDIT_MATCH + "')")
	@RequestMapping("{id}")
	public ModelAndView edit(@PathVariable("id") String matchId) {
		MatchCommand matchCommand = matchService.findByMatchId(matchId);
		return new ModelAndView(MATCH_PAGE, "matchCommand", matchCommand);
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_DELETE_MATCH + "')")
	@RequestMapping(value = "/delete/{matchId}", method = RequestMethod.GET)
	public ModelAndView deleteMatch(@PathVariable("matchId") String matchId, RedirectAttributes redirect) {
		LOG.debug("deleted match with id={}", matchId);

		MatchCommand matchCommand = matchService.findByMatchId(matchId);

		matchService.deleteMatch(matchId);

		messageUtil.addInfoMsg(redirect, "msg.match.deleted", matchCommand.getTeamNameOne(), matchCommand.getTeamNameTwo());

		return new ModelAndView("redirect:/matches");
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_CREATE_MATCH + "')")
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String createMatch(@ModelAttribute MatchCommand matchCommand) {
		matchCommand.setKickOffDate(LocalDateTime.now().plusHours(1));
		matchCommand.setMessageUtil(messageUtil);
		return MATCH_PAGE;
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_EDIT_MATCH + "')")
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView createOrUpdateMatch(@Valid MatchCommand matchCommand, BindingResult result, RedirectAttributes redirect,
			ModelMap modelMap) {
		matchCommand.setMessageUtil(messageUtil);
		if (result.hasErrors()) {
			return new ModelAndView(MATCH_PAGE, "formErrors", result.getAllErrors());
		}

		if (validate(matchCommand, modelMap)) {
			return new ModelAndView(MATCH_PAGE, "matchCommand", matchCommand);
		}

		if (StringUtils.isBlank(matchCommand.getMatchId())) {
			messageUtil.addInfoMsg(redirect, "msg.match.created", matchCommand.getTeamNameOne(), matchCommand.getTeamNameTwo());
		} else {
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

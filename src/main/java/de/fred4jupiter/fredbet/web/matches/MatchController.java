package de.fred4jupiter.fredbet.web.matches;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
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
	public ModelAndView list() {
		List<MatchCommand> matches = matchService.findAllMatches(securityBean.getCurrentUserName());
		ModelAndView modelAndView = new ModelAndView("matches/list", "allMatches", matches);
		modelAndView.addObject("heading", "alle Spiele");
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

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView createOrUpdate(@Valid MatchCommand matchCommand, BindingResult result, RedirectAttributes redirect,
			ModelMap modelMap) {
		if (result.hasErrors()) {
			return new ModelAndView("matches/form", "formErrors", result.getAllErrors());
		}

		if (validate(matchCommand, modelMap)) {
			return new ModelAndView("matches/form", "matchCommand", matchCommand);
		}

		matchService.save(matchCommand);

		String msg = "Spiel " + matchCommand.getTeamNameOne() + " gegen " + matchCommand.getTeamNameTwo() + " angelegt/aktualisiert!";
		messageUtil.addInfoMsg(redirect, msg);
		return new ModelAndView("redirect:/matches");
	}

	private boolean validate(MatchCommand matchCommand, ModelMap modelMap) {
		if (matchCommand.hasValidGoals()) {
			messageUtil.addErrorMsg(modelMap, "Negative Werte sind nicht erlaubt!");
			return true;
		}

		if ((matchCommand.isOnlyOneResultSet())) {
			messageUtil.addErrorMsg(modelMap, "Bitte geben Sie beide Tore an!");
			return true;
		}

		if (matchCommand.isDateOrTimeEmpty()) {
			messageUtil.addErrorMsg(modelMap, "Bitte geben Sie Datum und Uhrzeit ein!");
			return true;
		}
		
		if (matchCommand.isTeamNamesEmpty()) {
			messageUtil.addErrorMsg(modelMap, "Bitte geben Sie den Namen für Team 1 und Team 2 ein!");
			return true;
		}
		
		if (StringUtils.isEmpty(matchCommand.getStadium())) {
			messageUtil.addErrorMsg(modelMap, "Bitte geben Sie das Station ein!");
			return true;
		}

		return false;
	}
}

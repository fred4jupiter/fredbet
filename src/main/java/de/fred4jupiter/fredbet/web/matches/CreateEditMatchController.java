package de.fred4jupiter.fredbet.web.matches;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
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

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.service.CountryService;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;

@Controller
@RequestMapping("/match")
public class CreateEditMatchController {

	private static final Logger LOG = LoggerFactory.getLogger(CreateEditMatchController.class);

	private static final String VIEW_EDIT_MATCH = "matches/edit";

	@Autowired
	private MatchCommandMapper matchCommandMapper;

	@Autowired
	private WebMessageUtil messageUtil;

	@Autowired
	private MatchService matchService;

	@Autowired
	private CountryService countryService;

	@ModelAttribute("matchCommand")
	public MatchCommand matchCommand() {
		return new MatchCommand(messageUtil);
	}

	@ModelAttribute("availableGroups")
	public List<Group> availableGroups() {
		return Group.getAllGroups();
	}

	@ModelAttribute("availableCountries")
	public List<Country> availableCountries() {
		return countryService.getAvailableCountriesSortedWithNoneEntryByLocale(LocaleContextHolder.getLocale());
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_CREATE_MATCH + "')")
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String create(@ModelAttribute("matchCommand") MatchCommand matchCommand) {
		matchCommand.setKickOffDate(LocalDateTime.now().plusHours(1));
		return VIEW_EDIT_MATCH;
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_EDIT_MATCH + "')")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ModelAndView edit(@PathVariable("id") Long matchId) {
		MatchCommand matchCommand = matchCommandMapper.findByMatchId(matchId);
		return new ModelAndView(VIEW_EDIT_MATCH, "matchCommand", matchCommand);
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_EDIT_MATCH + "')")
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView save(@Valid MatchCommand matchCommand, BindingResult result, RedirectAttributes redirect, ModelMap modelMap) {
		if (result.hasErrors()) {
			return new ModelAndView(VIEW_EDIT_MATCH, "formErrors", result.getAllErrors());
		}

		if (validate(matchCommand, modelMap)) {
			return new ModelAndView(VIEW_EDIT_MATCH, "matchCommand", matchCommand);
		}

		if (matchCommand.getMatchId() == null) {
			messageUtil.addInfoMsg(redirect, "msg.match.created", matchCommand.getTeamNameOne(), matchCommand.getTeamNameTwo());
		} else {
			messageUtil.addInfoMsg(redirect, "msg.match.updated", matchCommand.getTeamNameOne(), matchCommand.getTeamNameTwo());
		}

		matchCommandMapper.save(matchCommand);
		return new ModelAndView("redirect:/matches#" + matchCommand.getMatchId());
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_DELETE_MATCH + "')")
	@RequestMapping(value = "/delete/{matchId}", method = RequestMethod.GET)
	public ModelAndView delete(@PathVariable("matchId") Long matchId, RedirectAttributes redirect) {
		LOG.debug("deleted match with id={}", matchId);

		MatchCommand matchCommand = matchCommandMapper.findByMatchId(matchId);

		matchService.deleteMatch(matchId);

		messageUtil.addInfoMsg(redirect, "msg.match.deleted", matchCommand.getTeamNameOne(), matchCommand.getTeamNameTwo());

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

		if (matchCommand.hasCountriesAndTeamNamesEntered()) {
			messageUtil.addErrorMsg(modelMap, "msg.input.countries.and.teamNames");
			return true;
		}

		if (matchCommand.hasSameTeamsPlayingAgainstEachOther()) {
			messageUtil.addErrorMsg(modelMap, "msg.input.same.teams");
			return true;
		}

		return false;
	}
}

package de.fred4jupiter.fredbet.web.matches;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

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
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.service.BettingService;
import de.fred4jupiter.fredbet.service.CountryService;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;

@Controller
@RequestMapping("/match")
public class CreateEditMatchController {

	private static final Logger LOG = LoggerFactory.getLogger(CreateEditMatchController.class);

	private static final String VIEW_EDIT_MATCH = "matches/edit";

	@Autowired
	private WebMessageUtil messageUtil;

	@Autowired
	private MatchService matchService;

	@Autowired
	private CountryService countryService;

	@Autowired
	private BettingService bettingService;

	@ModelAttribute("createEditMatchCommand")
	public CreateEditMatchCommand matchCommand() {
		return new CreateEditMatchCommand();
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
	public String create(@ModelAttribute("createEditMatchCommand") CreateEditMatchCommand createEditMatchCommand) {
		createEditMatchCommand.setKickOffDate(LocalDateTime.now().plusHours(1));
		return VIEW_EDIT_MATCH;
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_EDIT_MATCH + "')")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ModelAndView edit(@PathVariable("id") Long matchId) {
		Match match = matchService.findByMatchId(matchId);

		Long numberOfBetsForThisMatch = bettingService.countByMatch(match);
		CreateEditMatchCommand createEditMatchCommand = toCreateEditMatchCommand(match);
		if (numberOfBetsForThisMatch == 0) {
			createEditMatchCommand.setDeletable(true);
		}

		return new ModelAndView(VIEW_EDIT_MATCH, "createEditMatchCommand", createEditMatchCommand);
	}

	private CreateEditMatchCommand toCreateEditMatchCommand(Match match) {
		CreateEditMatchCommand command = new CreateEditMatchCommand();
		command.setCountryTeamOne(match.getCountryOne());
		command.setCountryTeamTwo(match.getCountryTwo());
		command.setTeamNameOne(match.getTeamNameOne());
		command.setTeamNameTwo(match.getTeamNameTwo());
		command.setGroup(match.getGroup());
		command.setKickOffDate(match.getKickOffDate());
		command.setMatchId(match.getId());
		command.setStadium(match.getStadium());
		return command;
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_EDIT_MATCH + "')")
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView save(@Valid CreateEditMatchCommand createEditMatchCommand, BindingResult result, RedirectAttributes redirect,
			ModelMap modelMap) {
		if (result.hasErrors()) {
			return new ModelAndView(VIEW_EDIT_MATCH, "createEditMatchCommand", createEditMatchCommand);
		}

		save(createEditMatchCommand);

		if (createEditMatchCommand.getMatchId() == null) {
			messageUtil.addInfoMsg(redirect, "msg.match.created", createEditMatchCommand.getTeamNameOne(),
					createEditMatchCommand.getTeamNameTwo());
		} else {
			messageUtil.addInfoMsg(redirect, "msg.match.updated", createEditMatchCommand.getTeamNameOne(),
					createEditMatchCommand.getTeamNameTwo());
		}

		return new ModelAndView("redirect:/matches#" + createEditMatchCommand.getMatchId());
	}

	private Long save(CreateEditMatchCommand createEditMatchCommand) {
		Match match = null;
		if (createEditMatchCommand.getMatchId() != null) {
			match = matchService.findByMatchId(createEditMatchCommand.getMatchId());
		}

		if (match == null) {
			match = new Match();
		}

		toMatch(createEditMatchCommand, match);

		match = matchService.save(match);
		createEditMatchCommand.setMatchId(match.getId());
		return match.getId();
	}

	public void toMatch(CreateEditMatchCommand matchCommand, Match match) {
		match.setCountryOne(matchCommand.getCountryTeamOne());
		match.setCountryTwo(matchCommand.getCountryTeamTwo());
		match.setTeamNameOne(matchCommand.getTeamNameOne());
		match.setTeamNameTwo(matchCommand.getTeamNameTwo());
		match.setKickOffDate(matchCommand.getKickOffDate());
		match.setGroup(matchCommand.getGroup());
		match.setStadium(matchCommand.getStadium());
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_DELETE_MATCH + "')")
	@RequestMapping(value = "/delete/{matchId}", method = RequestMethod.GET)
	public ModelAndView delete(@PathVariable("matchId") Long matchId, RedirectAttributes redirect) {
		LOG.debug("deleted match with id={}", matchId);

		Match match = matchService.findByMatchId(matchId);
		if (match == null) {
			messageUtil.addErrorMsg(redirect, "msg.match.notFound", matchId);
			return new ModelAndView("redirect:/matches");
		}

		matchService.deleteMatch(matchId);

		messageUtil.addInfoMsg(redirect, "msg.match.deleted", messageUtil.getTeamNameOne(match), messageUtil.getTeamNameTwo(match));

		return new ModelAndView("redirect:/matches");
	}

}

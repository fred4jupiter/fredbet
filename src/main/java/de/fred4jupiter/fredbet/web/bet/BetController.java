package de.fred4jupiter.fredbet.web.bet;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.service.BettingService;
import de.fred4jupiter.fredbet.service.CountryService;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.service.NoBettingAfterMatchStartedAllowedException;
import de.fred4jupiter.fredbet.util.Validator;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import de.fred4jupiter.fredbet.web.matches.MatchCommand;
import de.fred4jupiter.fredbet.web.matches.MatchCommandMapper;

@Controller
@RequestMapping("/bet")
public class BetController {

	private static final String VIEW_LIST_OPEN = "bet/list_open";

	private static final String VIEW_EDIT = "bet/edit";

	@Autowired
	private BettingService bettingService;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private WebMessageUtil messageUtil;

	@Autowired
	private MatchCommandMapper matchCommandMapper;

	@Autowired
	private CountryService countryService;

	@Autowired
	private MatchService matchService;
	
	@Autowired
	private AllBetsCommandMapper allBetsCommandMapper;

	@ModelAttribute("availableCountries")
	public List<Country> availableCountries() {
		return countryService.getAvailableCountriesSortedWithNoneEntryByLocale(LocaleContextHolder.getLocale());
	}

	@RequestMapping("/open")
	public ModelAndView listStillOpen(ModelMap modelMap) {
		List<Match> matchesToBet = bettingService.findMatchesToBet(securityService.getCurrentUserName());
		if (Validator.isEmpty(matchesToBet)) {
			messageUtil.addInfoMsg(modelMap, "msg.bet.betting.info.allBetted");
		}

		if (bettingService.hasOpenExtraBet(securityService.getCurrentUserName())) {
			messageUtil.addWarnMsg(modelMap, "msg.bet.betting.warn.extraBetOpen");
		}

		List<MatchCommand> matchCommands = matchesToBet.stream().map(match -> matchCommandMapper.toMatchCommand(match))
				.collect(Collectors.toList());
		return new ModelAndView(VIEW_LIST_OPEN, "matchesToBet", matchCommands);
	}

	@RequestMapping(value = "/createOrUpdate/{matchId}", method = RequestMethod.GET)
	public ModelAndView createOrUpdate(@PathVariable("matchId") Long matchId, @RequestParam(required = false) String redirectViewName) {
		Bet bet = bettingService.findOrCreateBetForMatch(matchId);
		if (bet == null) {
			return new ModelAndView("redirect:/matches");
		}

		BetCommand betCommand = toBetCommand(bet);
		betCommand.setRedirectViewName(redirectViewName);

		return new ModelAndView(VIEW_EDIT, "betCommand", betCommand);
	}

	private BetCommand toBetCommand(Bet bet) {
		BetCommand betCommand = new BetCommand();
		betCommand.setBetId(bet.getId());
		betCommand.setMatchId(bet.getMatch().getId());
		betCommand.setGoalsTeamOne(bet.getGoalsTeamOne());
		betCommand.setGoalsTeamTwo(bet.getGoalsTeamTwo());
		betCommand.setPenaltyWinnerOne(bet.isPenaltyWinnerOne());

		if (bet.getMatch().hasContriesSet()) {
			betCommand.setTeamNameOne(messageUtil.getCountryName(bet.getMatch().getCountryOne()));
			betCommand.setIconPathTeamOne(bet.getMatch().getCountryOne().getIconPath());

			betCommand.setTeamNameTwo(messageUtil.getCountryName(bet.getMatch().getCountryTwo()));
			betCommand.setIconPathTeamTwo(bet.getMatch().getCountryTwo().getIconPath());

			betCommand.setShowCountryIcons(true);
		} else {
			betCommand.setTeamNameOne(bet.getMatch().getTeamNameOne());
			betCommand.setTeamNameTwo(bet.getMatch().getTeamNameTwo());
		}

		betCommand.setGroupMatch(bet.getMatch().isGroupMatch());

		return betCommand;
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView createOrUpdate(@Valid BetCommand betCommand, BindingResult bindingResult, RedirectAttributes redirect,
			ModelMap modelMap) {
		if (bindingResult.hasErrors()) {
			return new ModelAndView(VIEW_EDIT, "betCommand", betCommand);
		}

		Bet bet = null;
		if (betCommand.getBetId() == null) {
			Match match = matchService.findMatchById(betCommand.getMatchId());
			bet = new Bet();
			bet.setMatch(match);
			bet.setUserName(securityService.getCurrentUserName());
		} else {
			bet = bettingService.findBetById(betCommand.getBetId());
		}

		bet.setGoalsTeamOne(betCommand.getGoalsTeamOne());
		bet.setGoalsTeamTwo(betCommand.getGoalsTeamTwo());
		bet.setPenaltyWinnerOne(betCommand.isPenaltyWinnerOne());

		try {
			bettingService.save(bet);
			messageUtil.addInfoMsg(redirect, "msg.bet.betting.created");
		} catch (NoBettingAfterMatchStartedAllowedException e) {
			messageUtil.addErrorMsg(redirect, "msg.bet.betting.error.matchInProgress");
		}

		String view = RedirectViewName.resolveRedirect(betCommand.getRedirectViewName());
		return new ModelAndView(view + "#" + betCommand.getMatchId());
	}

	@RequestMapping(value = "/others/match/{matchId}", method = RequestMethod.GET)
	public ModelAndView findBetsOfAllUsersByMatchId(@PathVariable("matchId") Long matchId) {
		AllBetsCommand allBetsCommand = allBetsCommandMapper.findAllBetsForMatchId(matchId);
		if (allBetsCommand == null) {
			return new ModelAndView("redirect:/matches");
		}
		return new ModelAndView("bet/others", "allBetsCommand", allBetsCommand);
	}

}

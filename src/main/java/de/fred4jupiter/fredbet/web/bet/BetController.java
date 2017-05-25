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
import de.fred4jupiter.fredbet.domain.ExtraBet;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.service.BettingService;
import de.fred4jupiter.fredbet.service.CountryService;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.service.NoBettingAfterMatchStartedAllowedException;
import de.fred4jupiter.fredbet.util.Validator;
import de.fred4jupiter.fredbet.web.MatchConverter;
import de.fred4jupiter.fredbet.web.MessageUtil;
import de.fred4jupiter.fredbet.web.RedirectViewName;
import de.fred4jupiter.fredbet.web.matches.MatchCommand;

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
	private MessageUtil messageUtil;

	@Autowired
	private MatchConverter matchConverter;

	@Autowired
	private CountryService countryService;

	@Autowired
	private MatchService matchService;

	@ModelAttribute("availableCountries")
	public List<Country> availableCountries() {
		return countryService.getAvailableCountriesSortedWithNoneEntryByLocale(LocaleContextHolder.getLocale());
	}

	@ModelAttribute("betCommand")
	public BetCommand betCommand() {
		return new BetCommand(messageUtil);
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

		List<MatchCommand> matchCommands = matchesToBet.stream().map(match -> matchConverter.toMatchCommand(match))
				.collect(Collectors.toList());
		return new ModelAndView(VIEW_LIST_OPEN, "matchesToBet", matchCommands);
	}

	@RequestMapping(value = "/createOrUpdate/{matchId}", method = RequestMethod.GET)
	public ModelAndView createOrUpdate(@PathVariable("matchId") Long matchId, @RequestParam(required = false) String redirectViewName) {
		BetCommand betCommand = bettingService.findOrCreateBetForMatch(matchId);
		if (betCommand == null) {
			return new ModelAndView("redirect:/matches");
		}
		betCommand.setRedirectViewName(redirectViewName);

		return new ModelAndView(VIEW_EDIT, "betCommand", betCommand);
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView createOrUpdate(@Valid BetCommand betCommand, BindingResult result, RedirectAttributes redirect, ModelMap modelMap) {
		Bet bet = null;
		if (betCommand.getBetId() == null) {
			Match match = matchService.findMatchById(betCommand.getMatchId());
			bet = new Bet();
			bet.setMatch(match);
			bet.setUserName(securityService.getCurrentUserName());
		} else {
			bet = bettingService.findBetById(betCommand.getBetId());
		}
		betCommand.setBet(bet);

		if (!betCommand.hasGoalsSet()) {
			messageUtil.addErrorMsg(modelMap, "msg.bet.betting.error.empty");
			return new ModelAndView(VIEW_EDIT, "betCommand", betCommand);
		}

		try {
			bet.setGoalsTeamOne(betCommand.getGoalsTeamOne());
			bet.setGoalsTeamTwo(betCommand.getGoalsTeamTwo());
			bet.setPenaltyWinnerOne(betCommand.isPenaltyWinnerOne());

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
		AllBetsCommand allBetsCommand = bettingService.findAllBetsForMatchId(matchId);
		if (allBetsCommand == null) {
			return new ModelAndView("redirect:/matches");
		}
		return new ModelAndView("bet/others", "allBetsCommand", allBetsCommand);
	}

	@RequestMapping(value = "/extra_bets", method = RequestMethod.GET)
	public ModelAndView showExtraBets() {
		ExtraBetCommand extraBetCommand = bettingService.loadExtraBetForUser(securityService.getCurrentUserName());
		return new ModelAndView("bet/extra_bets", "extraBetCommand", extraBetCommand);
	}

	@RequestMapping(value = "/extra_bets", method = RequestMethod.POST)
	public ModelAndView saveExtraBets(ExtraBetCommand extraBetCommand, RedirectAttributes redirect) {
		bettingService.saveExtraBet(extraBetCommand.getFinalWinner(), extraBetCommand.getSemiFinalWinner(),
				extraBetCommand.getThirdFinalWinner(), securityService.getCurrentUserName());

		messageUtil.addInfoMsg(redirect, "msg.bet.betting.created");
		return new ModelAndView("redirect:/bet/extra_bets");
	}

	@RequestMapping(value = "/extra_others", method = RequestMethod.GET)
	public ModelAndView showExtraBetResults() {
		List<ExtraBet> allExtraBets = bettingService.loadExtraBetDataOthers();
		return new ModelAndView("bet/extra_others", "allExtraBets", allExtraBets);
	}
}

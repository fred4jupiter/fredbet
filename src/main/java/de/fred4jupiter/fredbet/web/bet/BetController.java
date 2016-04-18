package de.fred4jupiter.fredbet.web.bet;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.ExtraBet;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.service.BettingService;
import de.fred4jupiter.fredbet.service.CountryService;
import de.fred4jupiter.fredbet.service.NoBettingAfterMatchStartedAllowedException;
import de.fred4jupiter.fredbet.web.MatchConverter;
import de.fred4jupiter.fredbet.web.MessageUtil;
import de.fred4jupiter.fredbet.web.RedirectViewName;
import de.fred4jupiter.fredbet.web.SecurityBean;
import de.fred4jupiter.fredbet.web.matches.MatchCommand;

@Controller
@RequestMapping("/bet")
public class BetController {

	private static final String VIEW_LIST_OPEN = "bet/list_open";

	private static final String VIEW_EDIT = "bet/edit";

	@Autowired
	private BettingService bettingService;

	@Autowired
	private SecurityBean securityBean;

	@Autowired
	private MessageUtil messageUtil;

	@Autowired
	private MatchConverter matchConverter;

	@Autowired
	private CountryService countryService;

	@ModelAttribute("availableCountries")
	public List<Country> availableCountries() {
		return countryService.getAvailableCountries();
	}

	@ModelAttribute("extraBetCommand")
	public ExtraBetCommand extraBetCommand() {
		return new ExtraBetCommand(messageUtil);
	}

	@RequestMapping("/open")
	public ModelAndView listStillOpen(ModelMap modelMap) {
		List<Match> matchesToBet = bettingService.findMatchesToBet(securityBean.getCurrentUserName());
		if (CollectionUtils.isEmpty(matchesToBet)) {
			messageUtil.addInfoMsg(modelMap, "msg.bet.betting.info.allBetted");
		}

		List<MatchCommand> matchCommands = matchesToBet.stream().map(match -> matchConverter.toMatchCommand(match))
				.collect(Collectors.toList());
		return new ModelAndView(VIEW_LIST_OPEN, "matchesToBet", matchCommands);
	}

	@RequestMapping(value = "/createOrUpdate/{matchId}", method = RequestMethod.GET)
	public ModelAndView createOrUpdate(@PathVariable("matchId") Long matchId, @RequestParam(required = false) String redirectViewName) {
		BetCommand betCommand = bettingService.findOrCreateBetForMatch(matchId);
		betCommand.setMessageUtil(messageUtil);
		betCommand.setRedirectViewName(redirectViewName);

		return new ModelAndView(VIEW_EDIT, "betCommand", betCommand);
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView createOrUpdate(@Valid BetCommand betCommand, BindingResult result, RedirectAttributes redirect, ModelMap modelMap) {
		betCommand.setMessageUtil(messageUtil);

		if (!betCommand.hasGoalsSet()) {
			messageUtil.addErrorMsg(modelMap, "msg.bet.betting.error.empty");
			return new ModelAndView(VIEW_EDIT, "betCommand", betCommand);
		}

		try {
			bettingService.save(betCommand);
			messageUtil.addInfoMsg(redirect, "msg.bet.betting.created");
		} catch (NoBettingAfterMatchStartedAllowedException e) {
			messageUtil.addErrorMsg(redirect, "msg.bet.betting.error.matchInProgress");
		}

		return new ModelAndView(RedirectViewName.resolveRedirect(betCommand.getRedirectViewName()));
	}

	@RequestMapping(value = "/others/match/{matchId}", method = RequestMethod.GET)
	public ModelAndView findBetsOfAllUsersByMatchId(@PathVariable("matchId") Long matchId) {
		AllBetsCommand allBetsCommand = bettingService.findAllBetsForMatchId(matchId);
		return new ModelAndView("bet/others", "allBetsCommand", allBetsCommand);
	}

	@RequestMapping(value = "/extra_bets", method = RequestMethod.GET)
	public ModelAndView showExtraBets() {
		ExtraBet extraBet = bettingService.findExtraBetOfUser(securityBean.getCurrentUserName());
		return new ModelAndView("bet/extra_bets", "extraBetCommand",
				new ExtraBetCommand(messageUtil, extraBet.getId(), extraBet.getFinalWinner(), extraBet.getSemiFinalWinner()));
	}

	@RequestMapping(value = "/extra_bets", method = RequestMethod.POST)
	public ModelAndView saveExtraBets(ExtraBetCommand extraBetCommand) {
		ExtraBet extraBet = bettingService.findExtraBetOfUser(securityBean.getCurrentUserName());
		extraBet.setFinalWinner(extraBetCommand.getFinalWinner());
		extraBet.setSemiFinalWinner(extraBetCommand.getSemiFinalWinner());
		extraBet.setUserName(securityBean.getCurrentUserName());

		bettingService.saveExtraBet(extraBet);
		return new ModelAndView("bet/extra_bets", "extraBetCommand", extraBetCommand);
	}
}

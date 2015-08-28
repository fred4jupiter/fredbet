package de.fred4jupiter.fredbet.web.bet;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.service.BettingService;
import de.fred4jupiter.fredbet.service.NoBettingAfterMatchStartedAllowedException;
import de.fred4jupiter.fredbet.web.MessageUtil;
import de.fred4jupiter.fredbet.web.SecurityBean;

@Controller
@RequestMapping("/bet")
public class BetController {

	@Autowired
	private BettingService bettingService;

	@Autowired
	private SecurityBean securityBean;
	
	@Autowired
	private MessageUtil messageUtil;

	@RequestMapping
	public ModelAndView list() {
		List<Bet> allBets = bettingService.findAllByUsername(securityBean.getCurrentUserName());
		return new ModelAndView("bet/list", "allBets", allBets);
	}

	@RequestMapping("/open")
	public ModelAndView listStillOpen(ModelMap modelMap) {
		List<Match> matchesToBet = bettingService.findMatchesToBet(securityBean.getCurrentUserName());
		if (CollectionUtils.isEmpty(matchesToBet)) {
			messageUtil.addInfoMsg(modelMap, "Alle Tipps abgegeben!");
		}
		
		return new ModelAndView("bet/list_open", "matchesToBet", matchesToBet);
	}

	@RequestMapping(value = "/createOrUpdate/{matchId}", method = RequestMethod.GET)
	public ModelAndView createOrUpdate(@PathVariable("matchId") String matchId) {
		BetCommand betCommand = bettingService.findOrCreateBetForMatch(matchId);

		return new ModelAndView("bet/form", "betCommand", betCommand);
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView createOrUpdate(@Valid BetCommand betCommand, BindingResult result, RedirectAttributes redirect) {
		if (result.hasErrors()) {
			return new ModelAndView("bet/form", "formErrors", result.getAllErrors());
		}

		try {
			bettingService.save(betCommand);
			messageUtil.addInfoMsg(redirect, "Tippspiel angelegt/aktualisiert!");
		} catch (NoBettingAfterMatchStartedAllowedException e) {
			messageUtil.addErrorMsg(redirect, "Das Spiel hat bereits begonnen! Keine Tippabgabe mehr möglich!");
		}

		return new ModelAndView("redirect:/matches");
	}
}

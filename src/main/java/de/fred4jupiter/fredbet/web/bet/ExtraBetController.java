package de.fred4jupiter.fredbet.web.bet;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.ExtraBet;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.service.BettingService;
import de.fred4jupiter.fredbet.service.CountryService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;

@Controller
@RequestMapping("/extra_bets")
public class ExtraBetController {

	@Autowired
	private BettingService bettingService;

	@Autowired
	private ExtraBetCommandMapper extraBetCommandMapper;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private WebMessageUtil messageUtil;

	@Autowired
	private CountryService countryService;

	@ModelAttribute("availableCountriesExtraBets")
	public List<Country> availableCountriesExtraBets() {
		return countryService.getAvailableCountriesExtraBetsSortedWithNoneEntryByLocale(LocaleContextHolder.getLocale());
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showExtraBets() {
		ExtraBet extraBet = bettingService.loadExtraBetForUser(securityService.getCurrentUserName());
		ExtraBetCommand extraBetCommand = extraBetCommandMapper.toExtraBetCommand(extraBet);
		return new ModelAndView("bet/extra_bets", "extraBetCommand", extraBetCommand);
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView saveExtraBets(ExtraBetCommand extraBetCommand, RedirectAttributes redirect) {
		bettingService.saveExtraBet(extraBetCommand.getFinalWinner(), extraBetCommand.getSemiFinalWinner(),
				extraBetCommand.getThirdFinalWinner(), securityService.getCurrentUserName());

		messageUtil.addInfoMsg(redirect, "msg.bet.betting.created");
		return new ModelAndView("redirect:/extra_bets");
	}
	
	@RequestMapping(value = "/others", method = RequestMethod.GET)
	public ModelAndView showExtraBetResults() {
		List<ExtraBet> allExtraBets = bettingService.loadExtraBetDataOthers();
		return new ModelAndView("bet/extra_others", "allExtraBets", allExtraBets);
	}

}

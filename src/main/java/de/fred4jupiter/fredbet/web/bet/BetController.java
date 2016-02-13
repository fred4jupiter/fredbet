package de.fred4jupiter.fredbet.web.bet;

import java.util.List;
import java.util.stream.Collectors;

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
import de.fred4jupiter.fredbet.web.MatchConverter;
import de.fred4jupiter.fredbet.web.MessageUtil;
import de.fred4jupiter.fredbet.web.SecurityBean;
import de.fred4jupiter.fredbet.web.matches.MatchCommand;

@Controller
@RequestMapping("/bet")
public class BetController {

	private static final String VIEW_LIST_OPEN = "bet/list_open";

	private static final String VIEW_LIST = "bet/list";

	private static final String VIEW_EDIT = "bet/edit";

	@Autowired
	private BettingService bettingService;

	@Autowired
	private SecurityBean securityBean;
	
	@Autowired
	private MessageUtil messageUtil;
	
	@Autowired
	private MatchConverter matchConverter;

	@RequestMapping
	public ModelAndView list() {
		List<Bet> allBets = bettingService.findAllByUsername(securityBean.getCurrentUserName());
		return new ModelAndView(VIEW_LIST, "allBets", allBets);
	}

	@RequestMapping("/open")
	public ModelAndView listStillOpen(ModelMap modelMap) {
		List<Match> matchesToBet = bettingService.findMatchesToBet(securityBean.getCurrentUserName());
		if (CollectionUtils.isEmpty(matchesToBet)) {
			messageUtil.addInfoMsg(modelMap, "msg.bet.betting.info.allBetted");
		}
		
		List<MatchCommand> matchCommands = matchesToBet.stream().map(match -> matchConverter.toMatchCommand(match)).collect(Collectors.toList());
		return new ModelAndView(VIEW_LIST_OPEN, "matchesToBet", matchCommands);
	}

	@RequestMapping(value = "/createOrUpdate/{matchId}", method = RequestMethod.GET)
	public ModelAndView createOrUpdate(@PathVariable("matchId") Long matchId) {
		BetCommand betCommand = bettingService.findOrCreateBetForMatch(matchId);
		betCommand.setMessageUtil(messageUtil);

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

		return new ModelAndView("redirect:/matches");
	}
}

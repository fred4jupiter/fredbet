package de.fred4jupiter.fredbet.web.bet;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Joker;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.service.BettingService;
import de.fred4jupiter.fredbet.service.JokerService;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.service.NoBettingAfterMatchStartedAllowedException;
import de.fred4jupiter.fredbet.util.Validator;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import de.fred4jupiter.fredbet.web.matches.MatchCommand;
import de.fred4jupiter.fredbet.web.matches.MatchCommandMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

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
    private MatchService matchService;

    @Autowired
    private AllBetsCommandMapper allBetsCommandMapper;

    @Autowired
    private JokerService jokerService;

    @GetMapping("/open")
    public String listStillOpen(Model model) {
        List<Match> matchesToBet = bettingService.findMatchesToBet(securityService.getCurrentUserName());
        if (Validator.isEmpty(matchesToBet)) {
            messageUtil.addInfoMsg(model, "msg.bet.betting.info.allBetted");
        }

        if (bettingService.hasOpenExtraBet(securityService.getCurrentUserName()) && !bettingService.hasFirstMatchStarted()) {
            messageUtil.addWarnMsg(model, "msg.bet.betting.warn.extraBetOpen");
        }

        List<MatchCommand> matchCommands = matchesToBet.stream().map(match -> matchCommandMapper.toMatchCommand(match))
                .collect(Collectors.toList());
        model.addAttribute("matchesToBet", matchCommands);
        return VIEW_LIST_OPEN;
    }

    @GetMapping("/createOrUpdate/{matchId}")
    public String showBet(@PathVariable("matchId") Long matchId, @RequestParam(required = false) String redirectViewName, Model model) {
        Bet bet = bettingService.findOrCreateBetForMatch(matchId);
        if (bet == null) {
            return "redirect:/matches";
        }

        BetCommand betCommand = toBetCommand(bet);
        betCommand.setRedirectViewName(redirectViewName);

        model.addAttribute("betCommand", betCommand);
        return VIEW_EDIT;
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
            betCommand.setIconPathTeamOne(bet.getMatch().getCountryOne().getIconPathBig());

            betCommand.setTeamNameTwo(messageUtil.getCountryName(bet.getMatch().getCountryTwo()));
            betCommand.setIconPathTeamTwo(bet.getMatch().getCountryTwo().getIconPathBig());

            betCommand.setShowCountryIcons(true);
        } else {
            betCommand.setTeamNameOne(bet.getMatch().getTeamNameOne());
            betCommand.setTeamNameTwo(bet.getMatch().getTeamNameTwo());
        }

        betCommand.setGroupMatch(bet.getMatch().isGroupMatch());

        Joker joker = jokerService.getJokerForUser(securityService.getCurrentUserName());
        betCommand.setNumberOfJokersUsed(joker.getNumberOfJokersUsed());
        betCommand.setMaxJokers(joker.getMax());
        betCommand.setUseJoker(bet.isJoker());
        betCommand.setJokerEditable(jokerService.isSettingJokerAllowed(securityService.getCurrentUserName(), bet.getMatch().getId()));

        return betCommand;
    }

    @PostMapping
    public String saveBet(@Valid BetCommand betCommand, BindingResult bindingResult, RedirectAttributes redirect) {
        if (bindingResult.hasErrors()) {
            return VIEW_EDIT;
        }

        try {
            bettingService.save(toBet(betCommand));
            messageUtil.addInfoMsg(redirect, "msg.bet.betting.created");
        } catch (NoBettingAfterMatchStartedAllowedException e) {
            messageUtil.addErrorMsg(redirect, "msg.bet.betting.error.matchInProgress");
        }

        String view = RedirectViewName.resolveRedirect(betCommand.getRedirectViewName());
        return view + "#" + betCommand.getMatchId();
    }

    private Bet toBet(BetCommand betCommand) {
        Bet bet;
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
        bet.setJoker(betCommand.isUseJoker());
        return bet;
    }

    @GetMapping("/others/match/{matchId}")
    public ModelAndView findBetsOfAllUsersByMatchId(@PathVariable("matchId") Long matchId, @RequestParam(required = false) String redirectViewName, RedirectAttributes redirect) {
        AllBetsCommand allBetsCommand = allBetsCommandMapper.findAllBetsForMatchId(matchId);
        if (allBetsCommand == null) {
            return new ModelAndView("redirect:/matches");
        } else if (allBetsCommand.getMatch().isBettable()) {
            messageUtil.addErrorMsg(redirect, "msg.match.bettable");
            return new ModelAndView("redirect:/matches", redirect.asMap());
        }

        allBetsCommand.setRedirectViewName(redirectViewName);
        return new ModelAndView("bet/others", "allBetsCommand", allBetsCommand);
    }
}

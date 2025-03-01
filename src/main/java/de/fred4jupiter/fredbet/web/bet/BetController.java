package de.fred4jupiter.fredbet.web.bet;

import de.fred4jupiter.fredbet.betting.ExtraBettingService;
import de.fred4jupiter.fredbet.domain.entity.Bet;
import de.fred4jupiter.fredbet.domain.Joker;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.betting.BettingService;
import de.fred4jupiter.fredbet.betting.JokerService;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.betting.NoBettingAfterMatchStartedAllowedException;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import de.fred4jupiter.fredbet.util.Validator;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import de.fred4jupiter.fredbet.web.matches.MatchCommand;
import de.fred4jupiter.fredbet.web.matches.MatchCommandMapper;
import jakarta.validation.Valid;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/bet")
public class BetController {

    private static final String VIEW_LIST_OPEN = "bet/list_open";

    private static final String VIEW_EDIT = "bet/edit";

    private final BettingService bettingService;

    private final SecurityService securityService;

    private final WebMessageUtil messageUtil;

    private final MessageSourceUtil messageSourceUtil;

    private final MatchCommandMapper matchCommandMapper;

    private final MatchService matchService;

    private final AllBetsCommandMapper allBetsCommandMapper;

    private final JokerService jokerService;

    private final ExtraBettingService extraBettingService;

    public BetController(BettingService bettingService, SecurityService securityService, WebMessageUtil messageUtil,
                         MessageSourceUtil messageSourceUtil, MatchCommandMapper matchCommandMapper, MatchService matchService,
                         AllBetsCommandMapper allBetsCommandMapper, JokerService jokerService, ExtraBettingService extraBettingService) {
        this.bettingService = bettingService;
        this.securityService = securityService;
        this.messageUtil = messageUtil;
        this.messageSourceUtil = messageSourceUtil;
        this.matchCommandMapper = matchCommandMapper;
        this.matchService = matchService;
        this.allBetsCommandMapper = allBetsCommandMapper;
        this.jokerService = jokerService;
        this.extraBettingService = extraBettingService;
    }

    @GetMapping("/open")
    public String listStillOpen(Model model) {
        List<Match> matchesToBet = bettingService.findMatchesToBet(securityService.getCurrentUserName());
        if (Validator.isEmpty(matchesToBet)) {
            messageUtil.addInfoMsg(model, "msg.bet.betting.info.allBetted");
        }

        if (extraBettingService.hasOpenExtraBet(securityService.getCurrentUserName()) && !bettingService.hasFirstMatchStarted()) {
            messageUtil.addWarnMsg(model, "msg.bet.betting.warn.extraBetOpen");
        }

        List<MatchCommand> matchCommands = matchesToBet.stream().map(matchCommandMapper::toMatchCommand).toList();
        model.addAttribute("matchesToBet", matchCommands);
        return VIEW_LIST_OPEN;
    }

    @GetMapping("/createOrUpdate/{matchId}")
    public String showBet(@PathVariable Long matchId, @RequestParam(required = false) String redirectViewName, Model model) {
        Bet bet = bettingService.findOrCreateBetForMatch(matchId);
        if (bet == null) {
            return "redirect:/matches";
        }

        BetCommand betCommand = toBetCommand(bet);
        betCommand.setRedirectViewName(redirectViewName);

        model.addAttribute("betCommand", betCommand);
        return VIEW_EDIT;
    }

    @GetMapping("/dice")
    public String diceAllMatches(Model model) {
        this.bettingService.diceAllMatchesForUser(securityService.getCurrentUserName());
        messageUtil.addInfoMsg(model, "msg.bet.betting.random.dice.all");
        return "redirect:/bet/open";
    }

    private BetCommand toBetCommand(Bet bet) {
        BetCommand betCommand = new BetCommand();
        betCommand.setBetId(bet.getId());
        betCommand.setMatchId(bet.getMatch().getId());
        betCommand.setGoalsTeamOne(bet.getGoalsTeamOne());
        betCommand.setGoalsTeamTwo(bet.getGoalsTeamTwo());
        betCommand.setPenaltyWinnerOne(bet.isPenaltyWinnerOne());

        final Locale locale = LocaleContextHolder.getLocale();
        final Team teamOne = bet.getMatch().getTeamOne();
        final Team teamTwo = bet.getMatch().getTeamTwo();
        betCommand.setTeamNameOne(teamOne.getNameTranslated(messageSourceUtil, locale));
        betCommand.setCountryTeamOne(teamOne.getCountry());

        betCommand.setTeamNameTwo(teamTwo.getNameTranslated(messageSourceUtil, locale));
        betCommand.setCountryTeamTwo(teamTwo.getCountry());

        betCommand.setGroupMatch(bet.getMatch().isGroupMatch());

        Joker joker = jokerService.getJokerForUser(securityService.getCurrentUserName());
        betCommand.setNumberOfJokersUsed(joker.numberOfJokersUsed());
        betCommand.setMaxJokers(joker.max());
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
    public ModelAndView findBetsOfAllUsersByMatchId(@PathVariable Long matchId, @RequestParam(required = false) String redirectViewName, RedirectAttributes redirect) {
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

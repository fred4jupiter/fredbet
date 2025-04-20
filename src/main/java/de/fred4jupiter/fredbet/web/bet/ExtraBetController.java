package de.fred4jupiter.fredbet.web.bet;

import de.fred4jupiter.fredbet.betting.ExtraBettingService;
import de.fred4jupiter.fredbet.country.CountryService;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.entity.ExtraBet;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/extrabets")
public class ExtraBetController {

    private final ExtraBetCommandMapper extraBetCommandMapper;

    private final SecurityService securityService;

    private final WebMessageUtil messageUtil;

    private final CountryService countryService;

    private final MatchService matchService;

    private final ExtraBettingService extraBettingService;

    public ExtraBetController(ExtraBetCommandMapper extraBetCommandMapper, SecurityService securityService,
                              WebMessageUtil messageUtil, CountryService countryService, MatchService matchService, ExtraBettingService extraBettingService) {
        this.extraBetCommandMapper = extraBetCommandMapper;
        this.securityService = securityService;
        this.messageUtil = messageUtil;
        this.countryService = countryService;
        this.matchService = matchService;
        this.extraBettingService = extraBettingService;
    }

    @ModelAttribute("availableCountriesExtraBets")
    public List<Country> availableCountriesExtraBets() {
        return countryService.getAvailableCountriesBasedOnMatches(LocaleContextHolder.getLocale());
    }

    @ModelAttribute("gameForThirdAvailable")
    public boolean gameForThirdAvailable() {
        return matchService.isGameForThirdAvailable();
    }

    @GetMapping
    public String showExtraBets(Model model) {
        ExtraBet extraBet = extraBettingService.loadExtraBetForUser(securityService.getCurrentUserName());
        ExtraBetCommand extraBetCommand = extraBetCommandMapper.toExtraBetCommand(extraBet);
        model.addAttribute("extraBetCommand", extraBetCommand);
        return "bet/extra_bets";
    }

    @PostMapping
    public String saveExtraBets(ExtraBetCommand extraBetCommand, RedirectAttributes redirect) {
        extraBettingService.saveExtraBet(extraBetCommand.getFinalWinner(), extraBetCommand.getSemiFinalWinner(),
            extraBetCommand.getThirdFinalWinner(), securityService.getCurrentUserName());

        messageUtil.addInfoMsg(redirect, "msg.bet.betting.created");
        return "redirect:/extrabets";
    }

    @GetMapping("/others")
    public ModelAndView showExtraBetResults() {
        List<ExtraBet> allExtraBets = extraBettingService.loadExtraBetDataOthers();
        return new ModelAndView("bet/extra_others", "allExtraBets", allExtraBets);
    }
}

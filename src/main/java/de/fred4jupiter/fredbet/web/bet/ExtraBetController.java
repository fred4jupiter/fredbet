package de.fred4jupiter.fredbet.web.bet;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.ExtraBet;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.service.BettingService;
import de.fred4jupiter.fredbet.service.CountryService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping
    public String showExtraBets(Model model) {
        ExtraBet extraBet = bettingService.loadExtraBetForUser(securityService.getCurrentUserName());
        ExtraBetCommand extraBetCommand = extraBetCommandMapper.toExtraBetCommand(extraBet);
        model.addAttribute("extraBetCommand", extraBetCommand);
        return "bet/extra_bets";
    }

    @PostMapping
    public String saveExtraBets(ExtraBetCommand extraBetCommand, RedirectAttributes redirect) {
        bettingService.saveExtraBet(extraBetCommand.getFinalWinner(), extraBetCommand.getSemiFinalWinner(),
                extraBetCommand.getThirdFinalWinner(), securityService.getCurrentUserName());

        messageUtil.addInfoMsg(redirect, "msg.bet.betting.created");
        return "redirect:/extra_bets";
    }

    @GetMapping("/others")
    public ModelAndView showExtraBetResults() {
        List<ExtraBet> allExtraBets = bettingService.loadExtraBetDataOthers();
        return new ModelAndView("bet/extra_others", "allExtraBets", allExtraBets);
    }
}

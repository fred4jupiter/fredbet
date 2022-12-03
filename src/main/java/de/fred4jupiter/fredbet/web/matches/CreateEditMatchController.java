package de.fred4jupiter.fredbet.web.matches;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.service.BettingService;
import de.fred4jupiter.fredbet.service.CountryService;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/match")
public class CreateEditMatchController {

    private static final Logger LOG = LoggerFactory.getLogger(CreateEditMatchController.class);

    private static final String VIEW_EDIT_MATCH = "matches/edit";

    private final WebMessageUtil webMessageUtil;

    private final MatchService matchService;

    private final CountryService countryService;

    private final BettingService bettingService;

    public CreateEditMatchController(WebMessageUtil webMessageUtil, MatchService matchService, CountryService countryService,
                                     BettingService bettingService) {
        this.webMessageUtil = webMessageUtil;
        this.matchService = matchService;
        this.countryService = countryService;
        this.bettingService = bettingService;
    }

    @PreAuthorize("hasAuthority('" + FredBetPermission.PERM_CREATE_MATCH + "')")
    @GetMapping("/create")
    public String create(Model model) {
        CreateEditMatchCommand command = new CreateEditMatchCommand();
        command.setKickOffDate(LocalDateTime.now().plusHours(1));
        model.addAttribute("createEditMatchCommand", command);
        addCountriesAndGroups(model);
        return VIEW_EDIT_MATCH;
    }

    @PreAuthorize("hasAuthority('" + FredBetPermission.PERM_EDIT_MATCH + "')")
    @GetMapping("/{id}")
    public String edit(@PathVariable("id") Long matchId, Model model) {
        Match match = matchService.findByMatchId(matchId);

        Long numberOfBetsForThisMatch = bettingService.countByMatch(match);
        CreateEditMatchCommand createEditMatchCommand = toCreateEditMatchCommand(match);
        if (numberOfBetsForThisMatch == 0) {
            createEditMatchCommand.setDeletable(true);
        }

        model.addAttribute("createEditMatchCommand", createEditMatchCommand);
        addCountriesAndGroups(model, match.getGroup());
        return VIEW_EDIT_MATCH;
    }

    @PreAuthorize("hasAuthority('" + FredBetPermission.PERM_EDIT_MATCH + "')")
    @PostMapping
    public String save(@Valid CreateEditMatchCommand createEditMatchCommand, BindingResult result, RedirectAttributes redirect, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("createEditMatchCommand", createEditMatchCommand);
            addCountriesAndGroups(model);
            return VIEW_EDIT_MATCH;
        }

        save(createEditMatchCommand);

        final String msgKey = createEditMatchCommand.getMatchId() == null ? "msg.match.created" : "msg.match.updated";

        String teamNameOne = webMessageUtil.getTeamName(createEditMatchCommand.getCountryTeamOne(), createEditMatchCommand.getTeamNameOne());
        String teamNameTwo = webMessageUtil.getTeamName(createEditMatchCommand.getCountryTeamTwo(), createEditMatchCommand.getTeamNameTwo());
        webMessageUtil.addInfoMsg(redirect, msgKey, teamNameOne, teamNameTwo);

        return "redirect:/matches#" + createEditMatchCommand.getMatchId();
    }

    @PreAuthorize("hasAuthority('" + FredBetPermission.PERM_DELETE_MATCH + "')")
    @GetMapping("/delete/{matchId}")
    public String delete(@PathVariable("matchId") Long matchId, RedirectAttributes redirect) {
        LOG.debug("deleted match with id={}", matchId);

        Match match = matchService.findByMatchId(matchId);
        if (match == null) {
            webMessageUtil.addErrorMsg(redirect, "msg.match.notFound", matchId);
            return "redirect:/matches";
        }

        matchService.deleteMatch(matchId);

        webMessageUtil.addInfoMsg(redirect, "msg.match.deleted", webMessageUtil.getTeamNameOne(match), webMessageUtil.getTeamNameTwo(match));

        return "redirect:/matches";
    }

    private void addCountriesAndGroups(Model model) {
        addCountriesAndGroups(model, null);
    }

    private void addCountriesAndGroups(Model model, Group group) {
        model.addAttribute("availableGroups", Group.getAllGroups());
        List<Country> countries = countryService.getAvailableCountriesSortedWithNoneEntryByLocale(LocaleContextHolder.getLocale(), group);
        model.addAttribute("availableCountries", countries);
    }

    private CreateEditMatchCommand toCreateEditMatchCommand(Match match) {
        CreateEditMatchCommand command = new CreateEditMatchCommand();
        command.setCountryTeamOne(match.getTeamOne().getCountry());
        command.setCountryTeamTwo(match.getTeamTwo().getCountry());
        command.setTeamNameOne(match.getTeamOne().getName());
        command.setTeamNameTwo(match.getTeamTwo().getName());
        command.setGroup(match.getGroup());
        command.setKickOffDate(match.getKickOffDate());
        command.setMatchId(match.getId());
        command.setStadium(match.getStadium());
        return command;
    }

    private Long save(CreateEditMatchCommand createEditMatchCommand) {
        Match match = null;
        if (createEditMatchCommand.getMatchId() != null) {
            match = matchService.findByMatchId(createEditMatchCommand.getMatchId());
        }

        if (match == null) {
            match = new Match();
        }

        toMatch(createEditMatchCommand, match);

        match = matchService.save(match);
        createEditMatchCommand.setMatchId(match.getId());
        return match.getId();
    }

    private void toMatch(CreateEditMatchCommand matchCommand, Match match) {
        match.getTeamOne().setCountry(matchCommand.getCountryTeamOne());
        match.getTeamTwo().setCountry(matchCommand.getCountryTeamTwo());
        match.getTeamOne().setName(matchCommand.getTeamNameOne());
        match.getTeamTwo().setName(matchCommand.getTeamNameTwo());
        match.setKickOffDate(matchCommand.getKickOffDate());
        match.setGroup(matchCommand.getGroup());
        match.setStadium(matchCommand.getStadium());
    }
}

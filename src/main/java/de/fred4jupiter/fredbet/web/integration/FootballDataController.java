package de.fred4jupiter.fredbet.web.integration;


import de.fred4jupiter.fredbet.data.DataService;
import de.fred4jupiter.fredbet.integration.*;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/footballdata")
@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_ADMINISTRATION + "')")
@SessionAttributes("footballDataCommand")
public class FootballDataController {

    private static final Logger LOG = LoggerFactory.getLogger(FootballDataController.class);

    private final FootballDataService footballDataService;

    private final FootballDataSyncService footballDataSyncService;

    private final FootballDataLoader footballDataLoader;

    private final WebMessageUtil webMessageUtil;

    private final DataService dataService;

    public FootballDataController(FootballDataService footballDataService, FootballDataSyncService footballDataSyncService,
                                  FootballDataLoader footballDataLoader,
                                  WebMessageUtil webMessageUtil, DataService dataService) {
        this.footballDataService = footballDataService;
        this.footballDataSyncService = footballDataSyncService;
        this.footballDataLoader = footballDataLoader;
        this.webMessageUtil = webMessageUtil;
        this.dataService = dataService;
    }

    @ModelAttribute("footballDataCommand")
    public FootballDataCommand initFootballDataCommand() {
        return new FootballDataCommand();
    }

    @ModelAttribute("footballDataSyncCommand")
    public FootballDataSyncCommand footballDataSyncCommand() {
        return new FootballDataSyncCommand();
    }

    @ModelAttribute("setupStatus")
    public FootballDataSetupStatus setupStatus() {
        return FootballDataSetupStatus.of(footballDataService.loadSettings());
    }

    @RequestMapping
    public String showPage(FootballDataCommand footballDataCommand, Model model) {
        final FootballDataRuntimeSettings settings = footballDataService.loadSettings();
        footballDataCommand.setEnabled(settings.isEnabled());
        footballDataCommand.setApiToken(settings.getApiToken());
        if (settings.getCompetition() != null) {
            footballDataCommand.setCompetitionId(settings.getCompetition().id());
            if (footballDataCommand.getCompetitions() == null) {
                footballDataCommand.setCompetitions(List.of(settings.getCompetition()));
            }
        }

        return "integration/footballdata";
    }

    @RequestMapping("/fetch-comp")
    public String fetchCompetitions(FootballDataCommand footballDataCommand, RedirectAttributes redirect) {
        if (!footballDataService.loadSettings().isReadyToFetchCompetitions()) {
            webMessageUtil.addErrorMsg(redirect, "footballdata.step.tournament.locked");
            return "redirect:/footballdata";
        }

        LOG.debug("fetching competitions...");
        List<Competition> competitions = footballDataLoader.loadCompetitions();
        footballDataCommand.setCompetitions(competitions);

        return "redirect:/footballdata";
    }

    @PostMapping("/save")
    public String save(@Valid FootballDataCommand footballDataCommand, BindingResult bindingResult, RedirectAttributes redirect, Model model) {
        if (bindingResult.hasErrors()) {
            return "integration/footballdata";
        }

        if (footballDataCommand.isEnabled() && StringUtils.isBlank(footballDataCommand.getApiToken())) {
            webMessageUtil.addErrorMsg(model, "footballdata.msg.apiTokenMissing");
            return "integration/footballdata";
        }

        final Competition competition = footballDataCommand.getCompetitionById(footballDataCommand.getCompetitionId());

        final FootballDataRuntimeSettings footballDataRuntimeSettings = footballDataService.loadSettings();
        footballDataRuntimeSettings.setEnabled(footballDataCommand.isEnabled());
        footballDataRuntimeSettings.setApiToken(footballDataCommand.getApiToken());
        footballDataRuntimeSettings.setCompetition(competition);

        footballDataService.saveSettings(footballDataRuntimeSettings);
        webMessageUtil.addInfoMsg(redirect, "footballdata.msg.saved");
        return "redirect:/footballdata";
    }

    @PostMapping("/sync")
    public String syncMatches(@Valid FootballDataSyncCommand command, BindingResult bindingResult, RedirectAttributes redirect, Model model) {
        if (bindingResult.hasErrors()) {
            return "integration/footballdata";
        }

        final FootballDataRuntimeSettings footballDataRuntimeSettings = footballDataService.loadSettings();
        if (!footballDataRuntimeSettings.isEnabled()) {
            LOG.info("Football data integration is disabled. Will not import or sync any data.");
            webMessageUtil.addInfoMsg(redirect, "footballdata.import.disabled");
            return "redirect:/footballdata";
        }

        if (!FootballDataSetupStatus.of(footballDataRuntimeSettings).competitionSelected()) {
            webMessageUtil.addErrorMsg(redirect, "footballdata.step.sync.locked");
            return "redirect:/footballdata";
        }

        try {
            Competition competition = footballDataRuntimeSettings.getCompetition();
            footballDataSyncService.syncData(competition, command.isForceUpdate());
            webMessageUtil.addInfoMsg(redirect, "footballdata.import.successful");
        } catch (FootballDataException e) {
            webMessageUtil.addErrorMsg(redirect, "error.msg", e.getMessage());
        }
        return "redirect:/footballdata";
    }

    @GetMapping("/deleteAllBetsAndMatches")
    public String deleteAllBetsAndMatches(RedirectAttributes redirect) {
        dataService.deleteAllBetsMatchesAndTeams();

        webMessageUtil.addInfoMsg(redirect, "datacreation.msg.info.allBetsAndMatchesDeleted");
        return "redirect:/footballdata";
    }
}

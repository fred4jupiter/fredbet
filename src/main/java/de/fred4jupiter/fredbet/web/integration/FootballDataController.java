package de.fred4jupiter.fredbet.web.integration;


import de.fred4jupiter.fredbet.data.DataPopulator;
import de.fred4jupiter.fredbet.integration.Competition;
import de.fred4jupiter.fredbet.integration.FootballDataService;
import de.fred4jupiter.fredbet.integration.FootballDataRuntimeSettings;
import de.fred4jupiter.fredbet.integration.FootballDataSyncService;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/footballdata")
@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_ADMINISTRATION + "')")
public class FootballDataController {

    private static final Logger LOG = LoggerFactory.getLogger(FootballDataController.class);

    private final FootballDataService footballDataService;

    private final FootballDataSyncService footballDataSyncService;

    private final WebMessageUtil webMessageUtil;

    private final DataPopulator dataPopulator;

    public FootballDataController(FootballDataService footballDataService, FootballDataSyncService footballDataSyncService,
                                  WebMessageUtil webMessageUtil, DataPopulator dataPopulator) {
        this.footballDataService = footballDataService;
        this.footballDataSyncService = footballDataSyncService;
        this.webMessageUtil = webMessageUtil;
        this.dataPopulator = dataPopulator;
    }

    @ModelAttribute("footballDataCommand")
    public FootballDataCommand initFootballDataCommand() {
        return new FootballDataCommand();
    }

    @RequestMapping
    public String showPage(FootballDataCommand footballDataCommand, Model model) {
        List<Competition> competitions = footballDataService.loadCompetitions();
        model.addAttribute("competitions", competitions);

        FootballDataRuntimeSettings settings = footballDataService.loadSettings();
        footballDataCommand.setEnabled(settings.isEnabled());
        footballDataCommand.setCompetitionKey(settings.getKey());

        return "integration/footballdata";
    }

    @RequestMapping(value = "/import")
    public String importMatches(RedirectAttributes redirect) {
        final FootballDataRuntimeSettings footballDataRuntimeSettings = footballDataService.loadSettings();
        if (!footballDataRuntimeSettings.isEnabled()) {
            LOG.info("Football data integration is disabled. Will not import or sync any data.");
            webMessageUtil.addInfoMsg(redirect, "footballdata.import.disabled");
            return "redirect:/footballdata";
        }

        int importedCount = footballDataSyncService.syncData(footballDataRuntimeSettings.getCompetitionCode(), footballDataRuntimeSettings.getSeasonYear());
        webMessageUtil.addInfoMsg(redirect, "footballdata.import.successful", importedCount);
        return "redirect:/footballdata";
    }

    @PostMapping("/save")
    public String save(@Valid FootballDataCommand footballDataCommand, BindingResult bindingResult, RedirectAttributes redirect) {
        if (bindingResult.hasErrors()) {
            return "integration/footballdata";
        }

        FootballDataRuntimeSettings footballDataRuntimeSettings = FootballDataRuntimeSettings.fromKey(footballDataCommand.isEnabled(), footballDataCommand.getCompetitionKey());

        footballDataService.saveSettings(footballDataRuntimeSettings);
        webMessageUtil.addInfoMsg(redirect, "msg.footballdata.saved");
        return "redirect:/footballdata";
    }

    @GetMapping("/deleteAllBetsAndMatches")
    public String deleteAllBetsAndMatches(RedirectAttributes redirect) {
        dataPopulator.executeAsync(DataPopulator::deleteAllBetsAndMatches);

        webMessageUtil.addInfoMsg(redirect, "administration.msg.info.allBetsAndMatchesDeleted");
        return "redirect:/footballdata";
    }

}

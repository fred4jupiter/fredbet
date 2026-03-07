package de.fred4jupiter.fredbet.web.integration;


import de.fred4jupiter.fredbet.integration.FootballDataService;
import de.fred4jupiter.fredbet.integration.FootballDataSettings;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/footballdata")
@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_ADMINISTRATION + "')")
public class FootballDataController {

    private static final Logger LOG = LoggerFactory.getLogger(FootballDataController.class);

    private final FootballDataService footballDataService;

    private final FootballDataSyncService footballDataSyncService;

    private final WebMessageUtil webMessageUtil;

    public FootballDataController(FootballDataService footballDataService, FootballDataSyncService footballDataSyncService, WebMessageUtil webMessageUtil) {
        this.footballDataService = footballDataService;
        this.footballDataSyncService = footballDataSyncService;
        this.webMessageUtil = webMessageUtil;
    }

    @ModelAttribute("footballDataCommand")
    public FootballDataCommand initFootballDataCommand() {
        return new FootballDataCommand();
    }

    @RequestMapping
    public String showPage(FootballDataCommand footballDataCommand, Model model) {
        FootballDataSettings settings = footballDataService.loadSettings();

        footballDataCommand.setEnabled(settings.isEnabled());
        footballDataCommand.setCompetitionCode(settings.getCompetitionCode());
        footballDataCommand.setSeasonYear(settings.getSeasonYear());

        return "integration/footballdata";
    }

    @RequestMapping(value = "/import")
    public String importMatches(RedirectAttributes redirect) {
        final FootballDataSettings footballDataSettings = footballDataService.loadSettings();
        if (!footballDataSettings.isEnabled()) {
            LOG.info("Football data integration is disabled. Will not import or sync any data.");
            webMessageUtil.addInfoMsg(redirect, "footballdata.import.disabled");
            return "redirect:/footballdata";
        }

        int importedCount = footballDataSyncService.syncData(footballDataSettings.getCompetitionCode(), footballDataSettings.getSeasonYear(), false);
        webMessageUtil.addInfoMsg(redirect, "footballdata.import.successful", importedCount);
        return "redirect:/footballdata";
    }

    @PostMapping("/save")
    public String save(@Valid FootballDataCommand footballDataCommand, BindingResult bindingResult, RedirectAttributes redirect) {
        if (bindingResult.hasErrors()) {
            return "integration/footballdata";
        }

        FootballDataSettings footballDataSettings = new FootballDataSettings();
        footballDataSettings.setEnabled(footballDataCommand.isEnabled());
        footballDataSettings.setCompetitionCode(footballDataCommand.getCompetitionCode());
        footballDataSettings.setSeasonYear(footballDataCommand.getSeasonYear());

        footballDataService.saveSettings(footballDataSettings);
        webMessageUtil.addInfoMsg(redirect, "msg.footballdata.saved");
        return "redirect:/footballdata";
    }
}

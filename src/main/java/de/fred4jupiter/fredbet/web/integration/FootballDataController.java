package de.fred4jupiter.fredbet.web.integration;


import de.fred4jupiter.fredbet.integration.FootballDataService;
import de.fred4jupiter.fredbet.integration.FootballDataSettings;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/footballdata")
@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_ADMINISTRATION + "')")
public class FootballDataController {

    private final FootballDataService footballDataService;

    private final WebMessageUtil webMessageUtil;

    public FootballDataController(FootballDataService footballDataService, WebMessageUtil webMessageUtil) {
        this.footballDataService = footballDataService;
        this.webMessageUtil = webMessageUtil;
    }

    @ModelAttribute("footballDataCommand")
    public FootballDataCommand initFootballDataCommand() {
        return new FootballDataCommand();
    }

    @RequestMapping
    public String showPage() {
        return "integration/footballdata";
    }

    @RequestMapping(value = "/import")
    public String importMatches(RedirectAttributes redirect) {
        int importedCount = footballDataService.syncData();
        webMessageUtil.addInfoMsg(redirect, "msg.footballdata.import.successful", importedCount);
        return "redirect:/footballdata";
    }

    @PostMapping("/save")
    public String save(@Valid FootballDataCommand footballDataCommand, BindingResult bindingResult, RedirectAttributes redirect) {
        if (bindingResult.hasErrors()) {
            return "integration/footballdata";
        }

        FootballDataSettings footballDataSettings = new FootballDataSettings();
        footballDataSettings.setEnabled(footballDataSettings.isEnabled());
        footballDataSettings.setCompetitionCode(footballDataCommand.getCompetitionCode());
        footballDataSettings.setSeasonYear(footballDataCommand.getSeasonYear());

        footballDataService.saveSettings(footballDataSettings);
        webMessageUtil.addInfoMsg(redirect, "msg.footballdata.saved");
        return "redirect:/footballdata";
    }
}

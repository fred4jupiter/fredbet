package de.fred4jupiter.fredbet.web.admin.integration;


import de.fred4jupiter.fredbet.integration.FootballDataService;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/footballdata")
@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_ADMINISTRATION + "')")
public class FootballDataController {

    private final FootballDataService footballDataService;

    public FootballDataController(FootballDataService footballDataService) {
        this.footballDataService = footballDataService;
    }

    @RequestMapping
    public String showPage(Model model) {
        return "integration/footballdata";
    }

    @RequestMapping(value = "/import")
    public String importMatches(RedirectAttributes redirectAttributes, Model model) {
        footballDataService.importData();
        return "redirect:/footballdata";
    }
}

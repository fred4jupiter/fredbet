package de.fred4jupiter.fredbet.web.integration;


import de.fred4jupiter.fredbet.integration.FootballDataService;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/footballdata")
@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_ADMINISTRATION + "')")
public class FootballDataController {

    private final FootballDataService footballDataService;

    private final WebMessageUtil messageUtil;

    public FootballDataController(FootballDataService footballDataService, WebMessageUtil messageUtil) {
        this.footballDataService = footballDataService;
        this.messageUtil = messageUtil;
    }

    @RequestMapping
    public String showPage() {
        return "integration/footballdata";
    }

    @RequestMapping(value = "/import")
    public String importMatches(RedirectAttributes redirect) {
        int importedCount = footballDataService.syncData();
        messageUtil.addInfoMsg(redirect, "msg.footballdata.import.successful", importedCount);
        return "redirect:/footballdata";
    }
}

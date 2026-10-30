package de.fred4jupiter.fredbet.web.admin;

import de.fred4jupiter.fredbet.admin.AdministrationService;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/administration/lastlogins")
@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_SHOW_LAST_LOGINS + "')")
public class LastLoginsController {

    private static final String PAGE_LAST_LOGINS = "admin/lastlogins";

    private final AdministrationService administrationService;

    public LastLoginsController(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    @GetMapping
    public String showLastLogins(Model model) {
        model.addAttribute("userList", administrationService.fetchLastLoginUsers());
        return PAGE_LAST_LOGINS;
    }
}

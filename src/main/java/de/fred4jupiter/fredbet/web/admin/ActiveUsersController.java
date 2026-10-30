package de.fred4jupiter.fredbet.web.admin;

import de.fred4jupiter.fredbet.admin.sessiontracking.SessionTrackingService;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/administration/active/users")
@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_SHOW_ACTIVE_USERS + "')")
public class ActiveUsersController {

    private static final String PAGE_ACTIVE_USERS = "admin/active_users";

    private final SessionTrackingService sessionTrackingService;

    public ActiveUsersController(SessionTrackingService sessionTrackingService) {
        this.sessionTrackingService = sessionTrackingService;
    }

    @GetMapping
    public String showActiveUsers(Model model) {
        model.addAttribute("userList", sessionTrackingService.findLoggedInUsers());
        return PAGE_ACTIVE_USERS;
    }
}

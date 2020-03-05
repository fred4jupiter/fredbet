package de.fred4jupiter.fredbet.web.admin;

import de.fred4jupiter.fredbet.data.AsyncDatabasePopulator;
import de.fred4jupiter.fredbet.data.DatabasePopulator;
import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.SessionTracking;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.service.admin.AdministrationService;
import de.fred4jupiter.fredbet.service.admin.SessionTrackingService;
import de.fred4jupiter.fredbet.service.user.UserService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/administration")
@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_ADMINISTRATION + "')")
public class AdminController {

    private static final String PAGE_ADMINISTRATION = "admin/administration";

    private static final String PAGE_ACTIVE_USERS = "admin/active_users";

    private static final String PAGE_LAST_LOGINS = "admin/lastlogins";

    @Autowired
    private DatabasePopulator databasePopulator;

    @Autowired
    private AsyncDatabasePopulator asyncDatabasePopulator;

    @Autowired
    private WebMessageUtil webMessageUtil;

    @Autowired
    private SessionTrackingService sessionTrackingService;

    @Autowired
    private AdministrationService administrationService;

    @Autowired
    private UserService userService;

    @ModelAttribute("adminFormCommand")
    public AdminFormCommand adminFormCommand() {
        return new AdminFormCommand();
    }

    @RequestMapping
    public String list() {
        return "admin/administration";
    }

    @GetMapping("/createRandomMatches")
    public String createRandomMatches(Model model) {
        asyncDatabasePopulator.createRandomMatches();
        webMessageUtil.addInfoMsg(model, "administration.msg.info.randomMatchesCreated");
        return PAGE_ADMINISTRATION;
    }

    @GetMapping("/createDemoBets")
    public String createDemoBets(Model model) {
        asyncDatabasePopulator.createDemoBetsForAllUsers();
        webMessageUtil.addInfoMsg(model, "administration.msg.info.demoBetsCreated");
        return PAGE_ADMINISTRATION;
    }

    @GetMapping("/createDemoResults")
    public String createDemoResults(Model model) {
        databasePopulator.createDemoResultsForAllMatches();
        webMessageUtil.addInfoMsg(model, "administration.msg.info.demoResultsCreated");
        return PAGE_ADMINISTRATION;
    }

    @GetMapping("/deleteAllBetsAndMatches")
    public String deleteAllBetsAndMatches(Model model) {
        databasePopulator.deleteAllBetsAndMatches();
        webMessageUtil.addInfoMsg(model, "administration.msg.info.allBetsAndMatchesDeleted");
        return PAGE_ADMINISTRATION;
    }

    @PreAuthorize("hasAuthority('" + FredBetPermission.PERM_SHOW_ACTIVE_USERS + "')")
    @GetMapping("/active/users")
    public String showActiveUsers(Model model) {
        List<SessionTracking> userList = sessionTrackingService.findLoggedInUsers();
        model.addAttribute("userList", userList);
        return PAGE_ACTIVE_USERS;
    }

    @PreAuthorize("hasAuthority('" + FredBetPermission.PERM_SHOW_LAST_LOGINS + "')")
    @GetMapping("/lastlogins")
    public String showLastLogins(Model model) {
        List<AppUser> lastLoginUsers = administrationService.fetchLastLoginUsers();
        model.addAttribute("userList", lastLoginUsers);
        return PAGE_LAST_LOGINS;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveRuntimeConfig(@Valid AdminFormCommand command, BindingResult bindingResult, RedirectAttributes redirect) {
        if (bindingResult.hasErrors()) {
            return PAGE_ADMINISTRATION;
        }

        asyncDatabasePopulator.createDemoUsers(command.getNumberOfTestUsers(), false);
        webMessageUtil.addInfoMsg(redirect, "administration.msg.info.testUsersCreated", command.getNumberOfTestUsers());
        return "redirect:/administration";
    }

    @GetMapping("/deleteAllUsers")
    public String deleteAllUsers(Model model) {
        userService.deleteAllUsers();
        webMessageUtil.addInfoMsg(model, "administration.msg.info.deleteAllUsers");
        return PAGE_ADMINISTRATION;
    }
}

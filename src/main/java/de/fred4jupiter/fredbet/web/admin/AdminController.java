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
import jakarta.validation.Valid;
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
@RequestMapping("/administration")
@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_ADMINISTRATION + "')")
public class AdminController {

    private static final String PAGE_ADMINISTRATION = "admin/administration";

    private static final String PAGE_ACTIVE_USERS = "admin/active_users";

    private static final String PAGE_LAST_LOGINS = "admin/lastlogins";

    private final DatabasePopulator databasePopulator;

    private final AsyncDatabasePopulator asyncDatabasePopulator;

    private final WebMessageUtil webMessageUtil;

    private final SessionTrackingService sessionTrackingService;

    private final AdministrationService administrationService;

    private final UserService userService;

    public AdminController(DatabasePopulator databasePopulator, AsyncDatabasePopulator asyncDatabasePopulator,
                           WebMessageUtil webMessageUtil, SessionTrackingService sessionTrackingService,
                           AdministrationService administrationService, UserService userService) {
        this.databasePopulator = databasePopulator;
        this.asyncDatabasePopulator = asyncDatabasePopulator;
        this.webMessageUtil = webMessageUtil;
        this.sessionTrackingService = sessionTrackingService;
        this.administrationService = administrationService;
        this.userService = userService;
    }

    @ModelAttribute("adminFormCommand")
    public AdminFormCommand adminFormCommand() {
        return new AdminFormCommand();
    }

    @RequestMapping
    public String list() {
        return PAGE_ADMINISTRATION;
    }

    @GetMapping("/createRandomMatches")
    public String createRandomMatches(RedirectAttributes redirect) {
        asyncDatabasePopulator.createRandomMatches();
        webMessageUtil.addInfoMsg(redirect, "administration.msg.info.randomMatchesCreated");
        return "redirect:/administration";
    }

    @GetMapping("/createDemoBets")
    public String createDemoBets(RedirectAttributes redirect) {
        asyncDatabasePopulator.createDemoBetsForAllUsers();
        webMessageUtil.addInfoMsg(redirect, "administration.msg.info.demoBetsCreated");
        return "redirect:/administration";
    }

    @GetMapping("/createDemoResults")
    public String createDemoResults(RedirectAttributes redirect) {
        asyncDatabasePopulator.createDemoResultsForAllMatches();
        webMessageUtil.addInfoMsg(redirect, "administration.msg.info.demoResultsCreated");
        return "redirect:/administration";
    }

    @GetMapping("/createTestDataForAll")
    public String createTestDataForAll(RedirectAttributes redirect) {
        asyncDatabasePopulator.createTestDataForAll();
        webMessageUtil.addInfoMsg(redirect, "administration.msg.info.createTestDataForAll");
        return "redirect:/administration";
    }

    @GetMapping("/deleteAllBetsAndMatches")
    public String deleteAllBetsAndMatches(RedirectAttributes redirect) {
        databasePopulator.deleteAllBetsAndMatches();
        webMessageUtil.addInfoMsg(redirect, "administration.msg.info.allBetsAndMatchesDeleted");
        return "redirect:/administration";
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

    @PostMapping("/save")
    public String saveRuntimeSettings(@Valid AdminFormCommand command, BindingResult bindingResult, RedirectAttributes redirect) {
        if (bindingResult.hasErrors()) {
            return PAGE_ADMINISTRATION;
        }

        asyncDatabasePopulator.createDemoUsers(command.getNumberOfTestUsers());
        webMessageUtil.addInfoMsg(redirect, "administration.msg.info.testUsersCreated", command.getNumberOfTestUsers());
        return "redirect:/administration";
    }

    @GetMapping("/deleteAllUsers")
    public String deleteAllUsers(RedirectAttributes redirect) {
        userService.deleteAllUsers();
        webMessageUtil.addInfoMsg(redirect, "administration.msg.info.deleteAllUsers");
        return "redirect:/administration";
    }
}

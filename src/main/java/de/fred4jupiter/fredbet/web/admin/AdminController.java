package de.fred4jupiter.fredbet.web.admin;

import de.fred4jupiter.fredbet.admin.AdministrationService;
import de.fred4jupiter.fredbet.admin.sessiontracking.SessionTrackingService;
import de.fred4jupiter.fredbet.data.DataPopulator;
import de.fred4jupiter.fredbet.data.DemoDataCreation;
import de.fred4jupiter.fredbet.data.GroupSelection;
import de.fred4jupiter.fredbet.domain.entity.AppUser;
import de.fred4jupiter.fredbet.domain.entity.SessionTracking;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.teambundle.TeamBundle;
import de.fred4jupiter.fredbet.user.UserService;
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

    private final DataPopulator dataPopulator;

    private final WebMessageUtil webMessageUtil;

    private final SessionTrackingService sessionTrackingService;

    private final AdministrationService administrationService;

    private final UserService userService;

    public AdminController(DataPopulator dataPopulator, WebMessageUtil webMessageUtil,
                           SessionTrackingService sessionTrackingService,
                           AdministrationService administrationService, UserService userService) {
        this.dataPopulator = dataPopulator;
        this.webMessageUtil = webMessageUtil;
        this.sessionTrackingService = sessionTrackingService;
        this.administrationService = administrationService;
        this.userService = userService;
    }

    @ModelAttribute("adminFormCommand")
    public AdminFormCommand adminFormCommand() {
        return new AdminFormCommand();
    }

    @ModelAttribute("testDataCommand")
    public TestDataCommand testDataCommand() {
        TestDataCommand testDataCommand = new TestDataCommand();
        testDataCommand.setGroupSelection(GroupSelection.ROUND_OF_SIXTEEN);
        return testDataCommand;
    }

    @GetMapping("/createDemoBets")
    public String createDemoBets(RedirectAttributes redirect) {
        dataPopulator.executeAsync(DataPopulator::createDemoBetsForAllUsers);
        webMessageUtil.addInfoMsg(redirect, "administration.msg.info.demoBetsCreated");
        return "redirect:/administration";
    }

    @GetMapping("/createDemoResults")
    public String createDemoResults(RedirectAttributes redirect) {
        dataPopulator.executeAsync(DataPopulator::createDemoResultsForAllMatches);
        webMessageUtil.addInfoMsg(redirect, "administration.msg.info.demoResultsCreated");
        return "redirect:/administration";
    }

    @RequestMapping
    public String list(Model model) {
        model.addAttribute("availableTeamBundles", TeamBundle.values());
        return PAGE_ADMINISTRATION;
    }

    @PostMapping("/testdata")
    public String createTestData(@Valid TestDataCommand command, BindingResult bindingResult, RedirectAttributes redirect) {
        if (bindingResult.hasErrors()) {
            return PAGE_ADMINISTRATION;
        }

        DemoDataCreation demoDataCreation = new DemoDataCreation(command.getGroupSelection(), command.getIncludeBets(),
            command.getIncludeResults(), command.getCreateGameOfThird());
        dataPopulator.executeAsync(populator -> populator.createDemoData(demoDataCreation));
        webMessageUtil.addInfoMsg(redirect, "administration.msg.info.demoDataCreated");
        return "redirect:/administration";
    }

    @GetMapping("/deleteAllBetsAndMatches")
    public String deleteAllBetsAndMatches(RedirectAttributes redirect) {
        dataPopulator.executeAsync(DataPopulator::deleteAllBetsAndMatches);

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
    public String createDemoUsers(@Valid AdminFormCommand command, BindingResult bindingResult, RedirectAttributes redirect) {
        if (bindingResult.hasErrors()) {
            return PAGE_ADMINISTRATION;
        }

        dataPopulator.executeAsync(populator -> populator.createDemoUsers(command.getNumberOfTestUsers()));
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

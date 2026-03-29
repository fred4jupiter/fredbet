package de.fred4jupiter.fredbet.web.admin;

import de.fred4jupiter.fredbet.admin.AdministrationService;
import de.fred4jupiter.fredbet.admin.sessiontracking.SessionTrackingService;
import de.fred4jupiter.fredbet.data.DataPopulator;
import de.fred4jupiter.fredbet.data.DemoDataCreation;
import de.fred4jupiter.fredbet.data.GroupSelection;
import de.fred4jupiter.fredbet.domain.entity.AppUser;
import de.fred4jupiter.fredbet.domain.entity.SessionTracking;
import de.fred4jupiter.fredbet.excel.ExcelReadingException;
import de.fred4jupiter.fredbet.integration.FootballDataSyncService;
import de.fred4jupiter.fredbet.integration.FootballDataTestingService;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.teambundle.TeamBundle;
import de.fred4jupiter.fredbet.user.UserService;
import de.fred4jupiter.fredbet.util.ResponseEntityUtil;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import de.fred4jupiter.fredbet.web.integration.FootballDataUploadCommand;
import jakarta.validation.Valid;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/administration")
@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_ADMINISTRATION + "')")
public class AdminController {

    private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);

    private static final String CONTENT_TYPE_JSON = "application/json";

    private static final String PAGE_ADMINISTRATION = "admin/administration";

    private static final String PAGE_ACTIVE_USERS = "admin/active_users";

    private static final String PAGE_LAST_LOGINS = "admin/lastlogins";

    private final DataPopulator dataPopulator;

    private final WebMessageUtil webMessageUtil;

    private final SessionTrackingService sessionTrackingService;

    private final AdministrationService administrationService;

    private final UserService userService;

    private final FootballDataTestingService footballDataTestingService;

    private final ApplicationContext applicationContext;

    public AdminController(DataPopulator dataPopulator, WebMessageUtil webMessageUtil,
                           SessionTrackingService sessionTrackingService,
                           AdministrationService administrationService, UserService userService, FootballDataTestingService footballDataTestingService,
                           ApplicationContext applicationContext) {
        this.dataPopulator = dataPopulator;
        this.webMessageUtil = webMessageUtil;
        this.sessionTrackingService = sessionTrackingService;
        this.administrationService = administrationService;
        this.userService = userService;
        this.footballDataTestingService = footballDataTestingService;
        this.applicationContext = applicationContext;
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

    @ModelAttribute("footballDataUploadCommand")
    public FootballDataUploadCommand footballDataUploadCommand() {
        return new FootballDataUploadCommand();
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

    @PostMapping("/upload")
    public String uploadFile(FootballDataUploadCommand footballDataUploadCommand, RedirectAttributes redirect, Model model) {
        final MultipartFile jsonFile = footballDataUploadCommand.getJsonFile();

        try {
            if (jsonFile == null || jsonFile.getBytes().length == 0) {
                webMessageUtil.addErrorMsg(redirect, "footballdata.upload.msg.noFileGiven");
                return "redirect:/administration";
            }

            if (!CONTENT_TYPE_JSON.equals(jsonFile.getContentType())) {
                webMessageUtil.addErrorMsg(redirect, "footballdata.upload.msg.noJsonFile");
                return "redirect:/administration";
            }

            footballDataTestingService.syncDataFromJson(jsonFile.getBytes());
            webMessageUtil.addInfoMsg(redirect, "footballdata.import.successful");
        } catch (IOException | ExcelReadingException e) {
            LOG.error(e.getMessage(), e);
            webMessageUtil.addErrorMsg(redirect, "footballdata.upload.msg.failed", e.getMessage());
        }

        return "redirect:/administration";
    }

    @GetMapping(value = "/download/template/{filename}", produces = CONTENT_TYPE_JSON)
    public ResponseEntity<byte[]> downloadTemplate(@PathVariable String filename) {
        Resource resource = applicationContext.getResource("classpath:football-data-json/" + filename);
        byte[] templateFile = downloadResource(resource);
        return ResponseEntityUtil.createResponseEntity(filename, templateFile, CONTENT_TYPE_JSON);
    }

    private byte[] downloadResource(Resource resource) {
        try {
            return IOUtils.toByteArray(resource.getInputStream());
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}

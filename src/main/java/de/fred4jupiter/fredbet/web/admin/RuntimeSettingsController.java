package de.fred4jupiter.fredbet.web.admin;

import de.fred4jupiter.fredbet.domain.NavbarLayout;
import de.fred4jupiter.fredbet.domain.Theme;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.settings.RuntimeSettingsService;
import de.fred4jupiter.fredbet.teambundle.TeamBundle;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import de.fred4jupiter.fredbet.web.util.TeamUtil;
import de.fred4jupiter.fredbet.web.util.TeamView;
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

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/runtimesettings")
@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_ADMINISTRATION + "')")
public class RuntimeSettingsController {

    private static final String PAGE_RUNTIME_CONFIG = "admin/runtime_settings";

    private final RuntimeSettingsService runtimeSettingsService;

    private final WebMessageUtil webMessageUtil;

    private final TeamUtil teamUtil;

    public RuntimeSettingsController(RuntimeSettingsService runtimeSettingsService, WebMessageUtil webMessageUtil,
                                     TeamUtil teamUtil) {
        this.runtimeSettingsService = runtimeSettingsService;
        this.webMessageUtil = webMessageUtil;
        this.teamUtil = teamUtil;
    }


    @ModelAttribute("availableTeams")
    public List<TeamView> availableTeams() {
        return teamUtil.getAvailableTeamsBasedOnMatches();
    }

    @ModelAttribute("availableThemes")
    public List<Theme> availableThemes() {
        return Arrays.asList(Theme.values());
    }

    @ModelAttribute("availableNavbarLayouts")
    public List<NavbarLayout> availableNavbarLayouts() {
        return Arrays.asList(NavbarLayout.values());
    }

    @GetMapping("/show")
    public String showPage(RuntimeSettingsCommand runtimeSettingsCommand, Model model) {
        runtimeSettingsCommand.setRuntimeSettings(runtimeSettingsService.loadRuntimeSettings());
        model.addAttribute("runtimeSettingsCommand", runtimeSettingsCommand);
        model.addAttribute("availableTeamBundles", TeamBundle.values());
        return PAGE_RUNTIME_CONFIG;
    }

    @PostMapping("/saveRuntimeSettings")
    public String saveRuntimeSettings(@Valid RuntimeSettingsCommand command, BindingResult bindingResult, RedirectAttributes redirect) {
        if (bindingResult.hasErrors()) {
            return PAGE_RUNTIME_CONFIG;
        }

        runtimeSettingsService.saveRuntimeSettings(command.getRuntimeSettings());

        webMessageUtil.addInfoMsg(redirect, "administration.msg.info.runtimeConfigSaved");

        return "redirect:/runtimesettings/show";
    }
}

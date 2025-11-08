package de.fred4jupiter.fredbet.web.admin;

import de.fred4jupiter.fredbet.domain.NavbarLayout;
import de.fred4jupiter.fredbet.domain.BootswatchTheme;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.settings.RuntimeSettings;
import de.fred4jupiter.fredbet.settings.RuntimeSettingsService;
import de.fred4jupiter.fredbet.teambundle.TeamBundle;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import de.fred4jupiter.fredbet.web.util.TeamUtil;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/show")
    public String showPage(RuntimeSettingsCommand runtimeSettingsCommand, Model model) {
        final RuntimeSettings runtimeSettings = runtimeSettingsService.loadRuntimeSettings();
        runtimeSettingsCommand.setRuntimeSettings(runtimeSettings);
        model.addAttribute("runtimeSettingsCommand", runtimeSettingsCommand);
        model.addAttribute("availableTeamBundles", TeamBundle.values());
        model.addAttribute("availableTeams", teamUtil.getAvailableTeams(runtimeSettings.getTeamBundle()));
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

    @HxRequest
    @GetMapping("/team-bundle")
    public String favouriteCountryOptions(@RequestParam(name = "runtimeSettings.teamBundle") TeamBundle teamBundle, Model model) {
        model.addAttribute("availableTeams", teamUtil.getAvailableTeams(teamBundle));
        return PAGE_RUNTIME_CONFIG + " :: favouriteCountryOptions";
    }
}

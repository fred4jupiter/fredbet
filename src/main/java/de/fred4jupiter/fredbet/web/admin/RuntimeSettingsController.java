package de.fred4jupiter.fredbet.web.admin;

import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.settings.RuntimeSettings;
import de.fred4jupiter.fredbet.settings.RuntimeSettingsService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/runtimesettings")
@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_ADMINISTRATION + "')")
public class RuntimeSettingsController {

    private static final String PAGE_RUNTIME_CONFIG = "admin/runtime_settings";

    private final RuntimeSettingsService runtimeSettingsService;

    private final WebMessageUtil webMessageUtil;

    public RuntimeSettingsController(RuntimeSettingsService runtimeSettingsService, WebMessageUtil webMessageUtil) {
        this.runtimeSettingsService = runtimeSettingsService;
        this.webMessageUtil = webMessageUtil;
    }

    @GetMapping("/show")
    public String showPage(RuntimeSettingsCommand runtimeSettingsCommand, Model model) {
        final RuntimeSettings runtimeSettings = runtimeSettingsService.loadRuntimeSettings();
        runtimeSettingsCommand.setRuntimeSettings(runtimeSettings);
        model.addAttribute("runtimeSettingsCommand", runtimeSettingsCommand);
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

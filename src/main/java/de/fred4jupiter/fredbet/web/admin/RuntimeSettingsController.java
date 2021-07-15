package de.fred4jupiter.fredbet.web.admin;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.service.CountryService;
import de.fred4jupiter.fredbet.service.config.RuntimeSettingsService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.springframework.context.i18n.LocaleContextHolder;
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
@RequestMapping("/runtimesettings")
@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_ADMINISTRATION + "')")
public class RuntimeSettingsController {

    private static final String PAGE_RUNTIME_CONFIG = "admin/runtime_settings";

    private final RuntimeSettingsService runtimeSettingsService;
    private final WebMessageUtil webMessageUtil;
    private final CountryService countryService;

    public RuntimeSettingsController(RuntimeSettingsService runtimeSettingsService, WebMessageUtil webMessageUtil,
                                     CountryService countryService) {
        this.runtimeSettingsService = runtimeSettingsService;
        this.webMessageUtil = webMessageUtil;
        this.countryService = countryService;
    }


    @ModelAttribute("availableCountries")
    public List<Country> availableCountries() {
        return countryService.getAvailableCountriesExtraBetsSortedWithNoneEntryByLocale(LocaleContextHolder.getLocale());
    }

    @GetMapping("/show")
    public String showPage(RuntimeSettingsCommand runtimeSettingsCommand, Model model) {
        runtimeSettingsCommand.setRuntimeSettings(runtimeSettingsService.loadRuntimeSettings());
        model.addAttribute("runtimeSettingsCommand", runtimeSettingsCommand);
        return PAGE_RUNTIME_CONFIG;
    }

    @RequestMapping(value = "/saveRuntimeSettings", method = RequestMethod.POST)
    public String saveRuntimeSettings(@Valid RuntimeSettingsCommand command, BindingResult bindingResult, RedirectAttributes redirect) {
        if (bindingResult.hasErrors()) {
            return PAGE_RUNTIME_CONFIG;
        }

        runtimeSettingsService.saveRuntimeSettings(command.getRuntimeSettings());

        webMessageUtil.addInfoMsg(redirect, "administration.msg.info.runtimeConfigSaved");

        return "redirect:/runtimesettings/show";
    }
}

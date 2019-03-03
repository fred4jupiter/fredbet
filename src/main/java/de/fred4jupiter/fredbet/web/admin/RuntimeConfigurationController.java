package de.fred4jupiter.fredbet.web.admin;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.service.CountryService;
import de.fred4jupiter.fredbet.service.config.RuntimeConfigurationService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.TimeZone;

@Controller
@RequestMapping("/runtimeconfig")
@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_ADMINISTRATION + "')")
public class RuntimeConfigurationController {

    private static final Logger LOG = LoggerFactory.getLogger(RuntimeConfigurationController.class);

    private static final String PAGE_RUNTIME_CONFIG = "admin/runtime_config";

    @Autowired
    private RuntimeConfigurationService runtimeConfigurationService;

    @Autowired
    private WebMessageUtil webMessageUtil;

    @Autowired
    private CountryService countryService;

    @ModelAttribute("availableCountries")
    public List<Country> availableCountries() {
        return countryService.getAvailableCountriesSortedWithoutNoneEntry(LocaleContextHolder.getLocale());
    }

    @ModelAttribute("runtimeConfigCommand")
    public RuntimeConfigCommand initRuntimeConfigCommand() {
        RuntimeConfigCommand configurationCommand = new RuntimeConfigCommand();
        configurationCommand.setTimeZone(TimeZone.getDefault().getID());
        return configurationCommand;
    }

    @GetMapping("/show")
    public String showCachePage(RuntimeConfigCommand runtimeConfigCommand, Model model) {
        runtimeConfigCommand.setRuntimeConfig(runtimeConfigurationService.loadRuntimeConfig());
        model.addAttribute("runtimeConfigCommand", runtimeConfigCommand);
        return PAGE_RUNTIME_CONFIG;
    }

    @RequestMapping(value = "/saveRuntimeConfig", method = RequestMethod.POST)
    public String saveRuntimeConfig(@Valid RuntimeConfigCommand command, BindingResult bindingResult, RedirectAttributes redirect,
                                    Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("runtimeConfigCommand", command);
            return PAGE_RUNTIME_CONFIG;
        }

        if (StringUtils.isNotBlank(command.getTimeZone())) {
            TimeZone timeZone = TimeZone.getTimeZone(command.getTimeZone());
            LOG.info("Setting timeZone to: {}", timeZone.getID());
            TimeZone.setDefault(timeZone);
        }

        runtimeConfigurationService.saveRuntimeConfig(command.getRuntimeConfig());

        webMessageUtil.addInfoMsg(redirect, "administration.msg.info.runtimeConfigSaved");

        return "redirect:/runtimeconfig/show";
    }
}

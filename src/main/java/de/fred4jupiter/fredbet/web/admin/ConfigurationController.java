package de.fred4jupiter.fredbet.web.admin;

import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.service.admin.CacheAdministrationService;
import de.fred4jupiter.fredbet.util.LogLevel;
import de.fred4jupiter.fredbet.util.LogLevelChangable;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/config")
@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_ADMINISTRATION + "')")
public class ConfigurationController {

    @Autowired
    private CacheAdministrationService cacheAdministrationService;

    @Autowired
    private WebMessageUtil webMessageUtil;

    @Autowired
    private LogLevelChangable logLevelChangable;

    @ModelAttribute("configurationCommand")
    public ConfigurationCommand initConfigurationCommand() {
        return new ConfigurationCommand();
    }

    @RequestMapping(value = "/show", method = RequestMethod.GET)
    public ModelAndView showCachePage(ConfigurationCommand configurationCommand) {
        configurationCommand.setLevel(logLevelChangable.getCurrentLogLevel());
        return new ModelAndView("admin/configuration", "configurationCommand", configurationCommand);
    }

    @RequestMapping(path = "/clearCache", method = RequestMethod.GET)
    public ModelAndView clearCache(ConfigurationCommand configurationCommand, ModelMap modelMap) {
        final ModelAndView modelAndView = new ModelAndView("admin/configuration");

        this.cacheAdministrationService.clearCaches();

        configurationCommand.setLevel(logLevelChangable.getCurrentLogLevel());

        webMessageUtil.addInfoMsg(modelMap, "administration.msg.info.cacheCleared");
        return modelAndView;
    }

    @RequestMapping(value = "/changeLogLevel", method = RequestMethod.POST)
    public ModelAndView changeLogLevel(@Valid ConfigurationCommand configurationCommand, RedirectAttributes redirect, ModelMap modelMap) {
        LogLevel level = configurationCommand.getLevel();

        logLevelChangable.setLogLevelTo(level);

        webMessageUtil.addInfoMsg(redirect, "administration.msg.info.logLevelChanged", level);

        return new ModelAndView("redirect:/config/show");
    }
}

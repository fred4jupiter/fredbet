package de.fred4jupiter.fredbet.web.admin;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.service.admin.CacheAdministrationService;
import de.fred4jupiter.fredbet.util.LoggingUtil;
import de.fred4jupiter.fredbet.util.LoggingUtil.LogLevel;
import de.fred4jupiter.fredbet.web.WebMessageUtil;

@Controller
@RequestMapping("/config")
@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_ADMINISTRATION + "')")
public class ConfigurationController {

	@Autowired
	private CacheAdministrationService cacheAdministrationService;

	@Autowired
	private WebMessageUtil messageUtil;

	@Autowired
	private LoggingUtil LoggingUtil;

	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public ModelAndView showCachePage() {
		ConfigurationCommand configurationCommand = new ConfigurationCommand();
		configurationCommand.setLevel(LoggingUtil.getCurrentLogLevel());
		return new ModelAndView("admin/configuration", "configurationCommand", configurationCommand);
	}

	@RequestMapping(path = "/clearCache", method = RequestMethod.GET)
	public ModelAndView clearCache(ModelMap modelMap) {
		final ModelAndView modelAndView = new ModelAndView("admin/configuration");

		this.cacheAdministrationService.clearCaches();

		messageUtil.addInfoMsg(modelMap, "administration.msg.info.cacheCleared");
		return modelAndView;
	}

	@RequestMapping(value = "/changeLogLevel", method = RequestMethod.POST)
	public ModelAndView changeLogLevel(@Valid ConfigurationCommand configurationCommand, RedirectAttributes redirect, ModelMap modelMap) {
		LogLevel level = configurationCommand.getLevel();

		LoggingUtil.setLogLevelTo(level);

		messageUtil.addInfoMsg(redirect, "administration.msg.info.logLevelChanged", level);
		
		return new ModelAndView("redirect:/config/show");
	}
}

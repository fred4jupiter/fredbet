package de.fred4jupiter.fredbet.web.admin;

import java.util.List;
import java.util.TimeZone;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.service.CountryService;
import de.fred4jupiter.fredbet.service.config.RuntimeConfigurationService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;

@Controller
@RequestMapping("/runtimeconfig")
@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_ADMINISTRATION + "')")
public class RuntimeConfigurationController {

	private static final Logger LOG = LoggerFactory.getLogger(RuntimeConfigurationController.class);

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

	@ModelAttribute("configurationCommand")
	public ConfigurationCommand initConfigurationCommand() {
		ConfigurationCommand configurationCommand = new ConfigurationCommand();
		configurationCommand.setTimeZone(TimeZone.getDefault().getID());
		return configurationCommand;
	}
	
	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public ModelAndView showCachePage(ConfigurationCommand configurationCommand) {
		configurationCommand.setRuntimeConfig(runtimeConfigurationService.loadRuntimeConfig());
		return new ModelAndView("admin/runtime_config", "configurationCommand", configurationCommand);
	}

	@RequestMapping(value = "/saveRuntimeConfig", method = RequestMethod.POST)
	public ModelAndView saveRuntimeConfig(@Valid ConfigurationCommand configurationCommand, BindingResult bindingResult,
			RedirectAttributes redirect, ModelMap modelMap) {
		if (bindingResult.hasErrors()) {
			return new ModelAndView("admin/runtime_config", "configurationCommand", configurationCommand);
		}

		if (StringUtils.isNotBlank(configurationCommand.getTimeZone())) {
			TimeZone timeZone = TimeZone.getTimeZone(configurationCommand.getTimeZone());
			LOG.info("Setting timeZone to: {}", timeZone.getID());
			TimeZone.setDefault(timeZone);
		}

		runtimeConfigurationService.saveRuntimeConfig(configurationCommand.getRuntimeConfig());

		webMessageUtil.addInfoMsg(redirect, "administration.msg.info.runtimeConfigSaved");

		return new ModelAndView("redirect:/runtimeconfig/show");
	}

}

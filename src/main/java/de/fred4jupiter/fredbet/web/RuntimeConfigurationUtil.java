package de.fred4jupiter.fredbet.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.domain.RuntimeConfig;
import de.fred4jupiter.fredbet.service.config.RuntimeConfigurationService;

/**
 * Helper class that will be accessed by the thymeleaf pages.
 * 
 * @author michael
 *
 */
@Component
public class RuntimeConfigurationUtil {

	@Autowired
	private RuntimeConfigurationService runtimeConfigurationService;

	public RuntimeConfig getConfig() {
		return runtimeConfigurationService.loadRuntimeConfig();
	}
}

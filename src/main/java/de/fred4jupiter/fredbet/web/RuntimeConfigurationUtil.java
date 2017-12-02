package de.fred4jupiter.fredbet.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.domain.RuntimeConfig;
import de.fred4jupiter.fredbet.service.config.RuntimeConfigurationService;

@Component
public class RuntimeConfigurationUtil {

	@Autowired
	private RuntimeConfigurationService runtimeConfigurationService;

	public boolean isEnabledParentChildRanking() {
		RuntimeConfig config = runtimeConfigurationService.loadRuntimeConfig();
		return config.isEnabledParentChildRanking();
	}
	
	public boolean isEnableChangingUsername() {
		RuntimeConfig config = runtimeConfigurationService.loadRuntimeConfig();
		return config.isEnableChangingUsername();
	}
}

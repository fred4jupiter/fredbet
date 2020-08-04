package de.fred4jupiter.fredbet.web;

import de.fred4jupiter.fredbet.domain.RuntimeSettings;
import de.fred4jupiter.fredbet.service.config.RuntimeSettingsService;
import org.springframework.stereotype.Component;

/**
 * Helper class that will be accessed by the thymeleaf pages.
 *
 * @author michael
 */
@Component
public class RuntimeConfigurationUtil {

    private final RuntimeSettingsService runtimeSettingsService;

    public RuntimeConfigurationUtil(RuntimeSettingsService runtimeSettingsService) {
        this.runtimeSettingsService = runtimeSettingsService;
    }

    public RuntimeSettings getConfig() {
        return runtimeSettingsService.loadRuntimeSettings();
    }
}

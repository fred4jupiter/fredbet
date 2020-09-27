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
public class RuntimeSettingsUtil {

    private final RuntimeSettingsService runtimeSettingsService;

    public RuntimeSettingsUtil(RuntimeSettingsService runtimeSettingsService) {
        this.runtimeSettingsService = runtimeSettingsService;
    }

    public RuntimeSettings getSettings() {
        return runtimeSettingsService.loadRuntimeSettings();
    }
}

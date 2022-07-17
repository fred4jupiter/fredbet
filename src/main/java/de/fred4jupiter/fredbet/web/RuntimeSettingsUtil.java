package de.fred4jupiter.fredbet.web;

import de.fred4jupiter.fredbet.domain.RuntimeSettings;
import de.fred4jupiter.fredbet.service.config.RuntimeSettingsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Helper class that will be accessed by the thymeleaf pages.
 *
 * @author michael
 */
@Component
public class RuntimeSettingsUtil {

    private final RuntimeSettingsService runtimeSettingsService;

    private final boolean h2ConsoleEnabled;

    public RuntimeSettingsUtil(RuntimeSettingsService runtimeSettingsService, @Value("${spring.h2.console.enabled:false}") boolean h2ConsoleEnabled) {
        this.runtimeSettingsService = runtimeSettingsService;
        this.h2ConsoleEnabled = h2ConsoleEnabled;
    }

    public RuntimeSettings getSettings() {
        return runtimeSettingsService.loadRuntimeSettings();
    }

    public boolean isH2ConsoleEnabled() {
        return h2ConsoleEnabled;
    }
}

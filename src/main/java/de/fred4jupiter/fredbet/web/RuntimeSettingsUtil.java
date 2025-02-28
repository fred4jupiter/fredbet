package de.fred4jupiter.fredbet.web;

import de.fred4jupiter.fredbet.settings.RuntimeSettings;
import de.fred4jupiter.fredbet.points.ExtraPointsConfiguration;
import de.fred4jupiter.fredbet.points.PointsConfigService;
import de.fred4jupiter.fredbet.settings.RuntimeSettingsService;
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

    private final PointsConfigService pointsConfigService;

    private final boolean h2ConsoleEnabled;

    public RuntimeSettingsUtil(RuntimeSettingsService runtimeSettingsService, PointsConfigService pointsConfigService,
                               @Value("${spring.h2.console.enabled:false}") boolean h2ConsoleEnabled) {
        this.runtimeSettingsService = runtimeSettingsService;
        this.pointsConfigService = pointsConfigService;
        this.h2ConsoleEnabled = h2ConsoleEnabled;
    }

    public RuntimeSettings getSettings() {
        return runtimeSettingsService.loadRuntimeSettings();
    }

    public ExtraPointsConfiguration getExtraPointsConfig() {
        return pointsConfigService.loadPointsConfig().getExtraPointsConfig();
    }

    public boolean isH2ConsoleEnabled() {
        return h2ConsoleEnabled;
    }
}

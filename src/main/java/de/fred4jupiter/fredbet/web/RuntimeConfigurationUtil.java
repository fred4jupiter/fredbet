package de.fred4jupiter.fredbet.web;

import de.fred4jupiter.fredbet.domain.RuntimeConfig;
import de.fred4jupiter.fredbet.service.config.RuntimeConfigurationService;
import org.springframework.stereotype.Component;

/**
 * Helper class that will be accessed by the thymeleaf pages.
 *
 * @author michael
 */
@Component
public class RuntimeConfigurationUtil {

    private final RuntimeConfigurationService runtimeConfigurationService;

    public RuntimeConfigurationUtil(RuntimeConfigurationService runtimeConfigurationService) {
        this.runtimeConfigurationService = runtimeConfigurationService;
    }

    public RuntimeConfig getConfig() {
        return runtimeConfigurationService.loadRuntimeConfig();
    }
}

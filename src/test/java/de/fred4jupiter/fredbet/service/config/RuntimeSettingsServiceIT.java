package de.fred4jupiter.fredbet.service.config;

import de.fred4jupiter.fredbet.common.TransactionalIntegrationTest;
import de.fred4jupiter.fredbet.domain.RuntimeSettings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TransactionalIntegrationTest
public class RuntimeSettingsServiceIT {

    @Autowired
    private RuntimeSettingsService runtimeSettingsService;

    @Test
    public void loadAndSaveConfiguration() {
        RuntimeSettings runtimeSettings = runtimeSettingsService.loadRuntimeSettings();
        assertNotNull(runtimeSettings);

        runtimeSettings.setEnabledParentChildRanking(true);

        runtimeSettingsService.saveRuntimeSettings(runtimeSettings);

        RuntimeSettings loaded = runtimeSettingsService.loadRuntimeSettings();
        assertNotNull(loaded);
        assertTrue(loaded.isEnabledParentChildRanking());
    }
}

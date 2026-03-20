package de.fred4jupiter.fredbet.settings;

import de.fred4jupiter.fredbet.common.TransactionalIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@TransactionalIntegrationTest
public class RuntimeSettingsServiceIT {

    @Autowired
    private RuntimeSettingsService runtimeSettingsService;

    @Test
    public void loadAndSaveConfiguration() {
        RuntimeSettings runtimeSettings = runtimeSettingsService.loadRuntimeSettings();
        assertThat(runtimeSettings).isNotNull();

        runtimeSettings.setEnabledParentChildRanking(true);

        runtimeSettingsService.saveRuntimeSettings(runtimeSettings);

        RuntimeSettings loaded = runtimeSettingsService.loadRuntimeSettings();
        assertThat(loaded).isNotNull();
        assertThat(loaded.isEnabledParentChildRanking()).isTrue();
    }
}

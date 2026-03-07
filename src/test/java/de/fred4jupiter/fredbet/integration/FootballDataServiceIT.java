package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.common.TransactionalIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@TransactionalIntegrationTest
public class FootballDataServiceIT {

    @Autowired
    private FootballDataService footballDataService;

    @Test
    void saveSettings() {
        FootballDataSettings settings = new FootballDataSettings();
        settings.setSeasonYear(1);
        settings.setCompetitionCode("EC");
        settings.setEnabled(true);

        footballDataService.saveSettings(settings);

        FootballDataSettings loadedSettings = footballDataService.loadSettings();
        assertThat(loadedSettings.getSeasonYear()).isEqualTo(settings.getSeasonYear());
        assertThat(loadedSettings.getCompetitionCode()).isEqualTo(settings.getCompetitionCode());
        assertThat(loadedSettings.isEnabled()).isEqualTo(settings.isEnabled());
    }
}

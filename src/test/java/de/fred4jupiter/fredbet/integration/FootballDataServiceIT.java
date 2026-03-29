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
        FootballDataRuntimeSettings settings = new FootballDataRuntimeSettings();
        Competition competition = new Competition(1, "European Championship", "EC", 2024);
        settings.setCompetition(competition);
        settings.setEnabled(true);

        footballDataService.saveSettings(settings);

        FootballDataRuntimeSettings loadedSettings = footballDataService.loadSettings();
        assertThat(loadedSettings).isNotNull();
        assertThat(loadedSettings.isEnabled()).isEqualTo(settings.isEnabled());

        Competition loadedCompetition = loadedSettings.getCompetition();
        assertThat(loadedCompetition).isNotNull();
        assertThat(loadedCompetition).isEqualTo(competition);
    }
}

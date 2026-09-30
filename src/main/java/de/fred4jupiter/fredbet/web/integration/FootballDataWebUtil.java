package de.fred4jupiter.fredbet.web.integration;

import de.fred4jupiter.fredbet.integration.FootballDataRuntimeSettings;
import de.fred4jupiter.fredbet.integration.FootballDataService;
import org.springframework.stereotype.Component;

@Component
public class FootballDataWebUtil {

    private final FootballDataService footballDataService;

    public FootballDataWebUtil(FootballDataService footballDataService) {
        this.footballDataService = footballDataService;
    }

    public boolean isCupCompetitionEnabled() {
        final FootballDataRuntimeSettings settings = footballDataService.loadSettings();
        return settings.isEnabled() && settings.getCompetition().isCupCompetition();
    }
}

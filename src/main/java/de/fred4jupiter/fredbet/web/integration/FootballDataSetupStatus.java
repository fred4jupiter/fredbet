package de.fred4jupiter.fredbet.web.integration;

import de.fred4jupiter.fredbet.integration.FootballDataRuntimeSettings;

/**
 * Setup steps of the football data integration. Each step requires the previous one to be saved.
 */
public record FootballDataSetupStatus(boolean activated, boolean competitionSelected) {

    public static FootballDataSetupStatus of(FootballDataRuntimeSettings settings) {
        final boolean activated = settings.isReadyToFetchCompetitions();
        return new FootballDataSetupStatus(activated, activated && settings.getCompetition() != null);
    }
}

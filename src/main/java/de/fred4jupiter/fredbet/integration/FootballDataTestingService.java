package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.data.DataPopulator;
import de.fred4jupiter.fredbet.integration.model.FdMatches;
import de.fred4jupiter.fredbet.util.JsonObjectConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FootballDataTestingService {

    private static final Logger LOG = LoggerFactory.getLogger(FootballDataTestingService.class);

    private final FootballDataSyncService footballDataSyncService;

    private final FootballDataService footballDataService;

    private final JsonObjectConverter jsonObjectConverter;

    private final DataPopulator dataPopulator;

    public FootballDataTestingService(FootballDataSyncService footballDataSyncService,
                                      FootballDataService footballDataService,
                                      JsonObjectConverter jsonObjectConverter, DataPopulator dataPopulator) {
        this.footballDataSyncService = footballDataSyncService;
        this.footballDataService = footballDataService;
        this.jsonObjectConverter = jsonObjectConverter;
        this.dataPopulator = dataPopulator;
    }

    public void syncDataFromJson(byte[] fsMatchesAsJson) {
        final FootballDataRuntimeSettings settings = footballDataService.loadSettings();
        if (settings.isEnabled()) {
            settings.setEnabled(false);
            footballDataService.saveSettings(settings);
            LOG.info("disabled Football Data integration job when using test data.");
        }

        final FdMatches fdMatches = jsonObjectConverter.fromJson(new String(fsMatchesAsJson), FdMatches.class);
        if (fdMatches == null || fdMatches.matches() == null || fdMatches.matches().isEmpty()) {
            LOG.warn("Could not load football data fdMatches from json!");
            return;
        }

        if (fdMatches.hasMatches()) {
            // delete all bets and matches before importing the new ones...
            dataPopulator.deleteAllBetsAndMatches();
            LOG.info("deleted all bets and matches");
        }

        // if competition completed then import matches first, add bets and then import with results again
        if (fdMatches.isCompetitionCompleted()) {
            FdMatches fdMatchesWithoutResults = fdMatches.createNewWithoutResults();
            footballDataSyncService.syncData(fdMatchesWithoutResults, false);

            dataPopulator.createDemoBetsForAllUsers();
        }
        footballDataSyncService.syncData(fdMatches.createNewWithUpdatedTimestamp(), false);
    }
}

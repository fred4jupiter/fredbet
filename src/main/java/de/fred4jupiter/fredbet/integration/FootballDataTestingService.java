package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.integration.model.FdMatches;
import de.fred4jupiter.fredbet.util.JsonObjectConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FootballDataTestingService {

    private static final Logger LOG = LoggerFactory.getLogger(FootballDataTestingService.class);

    private final FootballDataSyncService footballDataSyncService;

    private final JsonObjectConverter jsonObjectConverter;

    public FootballDataTestingService(FootballDataSyncService footballDataSyncService, JsonObjectConverter jsonObjectConverter) {
        this.footballDataSyncService = footballDataSyncService;
        this.jsonObjectConverter = jsonObjectConverter;
    }

    public void syncDataFromJson(byte[] fsMatchesAsJson) {
        final FdMatches fdMatches = jsonObjectConverter.fromJson(new String(fsMatchesAsJson), FdMatches.class);
        if (fdMatches == null || fdMatches.matches() == null || fdMatches.matches().isEmpty()) {
            LOG.warn("Could not load football data fdMatches from json!");
            return;
        }

        footballDataSyncService.syncData(fdMatches);
    }
}

package de.fred4jupiter.fredbet.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MatchUpdaterJob {

    private static final Logger LOG = LoggerFactory.getLogger(MatchUpdaterJob.class);

    private final FootballDataSyncService footballDataSyncService;

    private final FootballDataService footballDataService;

    public MatchUpdaterJob(FootballDataSyncService footballDataSyncService, FootballDataService footballDataService) {
        this.footballDataSyncService = footballDataSyncService;
        this.footballDataService = footballDataService;
    }

    @Scheduled(fixedRate = 10 * 60 * 1000) // every 10 minutes
    public void updateMatches() {
        final FootballDataSettings footballDataSettings = footballDataService.loadSettings();
        if (!footballDataSettings.isEnabled()) {
            LOG.info("Football data settings disabled. Do not sync any matches...");
            return;
        }

        footballDataSyncService.syncData(footballDataSettings.getCompetitionCode(), footballDataSettings.getSeasonYear());
    }
}

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

    @Scheduled(fixedRateString = "#{${fredbet.integration.football-data.scheduler-interval-minutes} * 60 * 1000}")
    public void updateMatches() {
        final FootballDataRuntimeSettings footballDataRuntimeSettings = footballDataService.loadSettings();
        if (!footballDataRuntimeSettings.isEnabled()) {
            LOG.info("Football data settings disabled. Do not sync any matches...");
            return;
        }

        LOG.info("start syncing data from football-data for footballDataSettings={}", footballDataRuntimeSettings);

        // footballDataSyncService.syncData(footballDataSettings.getCompetitionCode(), footballDataSettings.getSeasonYear());
    }
}

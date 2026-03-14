package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.admin.CacheAdministrationService;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.integration.model.FdMatch;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.props.CacheNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FootballDataSyncService {

    private static final Logger LOG = LoggerFactory.getLogger(FootballDataSyncService.class);

    private final FootballDataRestClient footballDataRestClient;

    private final MatchService matchService;

    private final FdMatchConverter fdMatchConverter;

    private final CacheAdministrationService administrationService;

    FootballDataSyncService(FootballDataRestClient footballDataRestClient,
                            MatchService matchService, FdMatchConverter fdMatchConverter, CacheAdministrationService administrationService) {
        this.footballDataRestClient = footballDataRestClient;
        this.matchService = matchService;
        this.fdMatchConverter = fdMatchConverter;
        this.administrationService = administrationService;
    }

    public void syncData(String competitionCode, int seasonYear) {
        final List<FdMatch> matches = footballDataRestClient.fetchMatches(competitionCode, seasonYear);
        if (matches == null || matches.isEmpty()) {
            LOG.warn("Could not load football data fdMatchesList!");
            return;
        }

        matches.forEach(this::syncMatch);
        administrationService.clearCacheByCacheName(CacheNames.AVAIL_GROUPS);

        LOG.info("synced {} matches.", matches.size());
    }

    private void syncMatch(FdMatch fdMatch) {
        final Match match = matchService.findByExternalId(fdMatch.id()).orElse(new Match());
        fdMatchConverter.mapAndSave(fdMatch, match);
    }
}

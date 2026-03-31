package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.admin.CacheAdministrationService;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.integration.model.FdMatch;
import de.fred4jupiter.fredbet.integration.model.FdMatches;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.props.CacheNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FootballDataSyncService {

    private static final Logger LOG = LoggerFactory.getLogger(FootballDataSyncService.class);

    private final FootballDataLoader footballDataLoader;

    private final MatchService matchService;

    private final FdMatchSyncImporter fdMatchSyncImporter;

    private final CacheAdministrationService administrationService;

    FootballDataSyncService(FootballDataLoader footballDataLoader, MatchService matchService, FdMatchSyncImporter fdMatchSyncImporter,
                            CacheAdministrationService administrationService) {
        this.footballDataLoader = footballDataLoader;
        this.matchService = matchService;
        this.fdMatchSyncImporter = fdMatchSyncImporter;
        this.administrationService = administrationService;
    }

    public void syncData(Competition competition) {
        LOG.info("*** start syncing football data for competition {}", competition);
        syncData(footballDataLoader.fetchMatches(competition));
        LOG.info("*** end syncing football data");
    }

    public void syncData(FdMatches fdMatches) {
        if (fdMatches == null) {
            LOG.warn("Could not load football data fdMatchesList!");
            return;
        }

        final List<FdMatch> matches = fdMatches.matches();
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
        fdMatchSyncImporter.mapAndSave(fdMatch, match);
    }
}

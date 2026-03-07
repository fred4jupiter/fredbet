package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.integration.model.FdMatch;
import de.fred4jupiter.fredbet.match.MatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class FootballDataSyncService {

    private static final Logger LOG = LoggerFactory.getLogger(FootballDataSyncService.class);

    private final FootballDataRestClient footballDataRestClient;

    private final MatchService matchService;

    private final FdMatchConverter fdMatchConverter;

    FootballDataSyncService(FootballDataRestClient footballDataRestClient,
                            MatchService matchService, FdMatchConverter fdMatchConverter) {
        this.footballDataRestClient = footballDataRestClient;
        this.matchService = matchService;
        this.fdMatchConverter = fdMatchConverter;
    }

    public int syncData(String competitionCode, int seasonYear) {
        List<FdMatch> matches = footballDataRestClient.fetchMatches(competitionCode, seasonYear);
        return syncData(matches);
    }

    private int syncData(List<FdMatch> fdMatches) {
        if (fdMatches == null || fdMatches.isEmpty()) {
            LOG.warn("Could not load football data fdMatchesList!");
            return 0;
        }
        LOG.debug("Syncing {} Football-Data fdMatchesList", fdMatches.size());

        final List<Match> matches = fdMatches.stream()
            .map(fdMatchConverter::mapToMatch)
            .filter(Objects::nonNull)
            .toList();
        matchService.saveAll(matches);

        LOG.debug("synced {} matches", matches.size());
        return matches.size();
    }
}

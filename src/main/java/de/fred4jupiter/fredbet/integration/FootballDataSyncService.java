package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.integration.model.FdMatch;
import de.fred4jupiter.fredbet.match.MatchService;
import org.jspecify.annotations.NonNull;
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

    FootballDataSyncService(FootballDataRestClient footballDataRestClient,
                            MatchService matchService, FdMatchConverter fdMatchConverter) {
        this.footballDataRestClient = footballDataRestClient;
        this.matchService = matchService;
        this.fdMatchConverter = fdMatchConverter;
    }

    public int syncData(String competitionCode, int seasonYear) {
        final List<FdMatch> matches = footballDataRestClient.fetchMatches(competitionCode, seasonYear);
        if (matches == null || matches.isEmpty()) {
            LOG.warn("Could not load football data fdMatchesList!");
            return 0;
        }

        final List<Match> updatedMatches = matches.stream()
            .map(this::syncMatch)
            .filter(match -> match.getExternalId() != null)
            .toList();

        matchService.saveAll(updatedMatches);
        LOG.info("saved {} matches.", updatedMatches.size());
        return updatedMatches.size();
    }

    private @NonNull Match syncMatch(FdMatch fdMatch) {
        final Match match = matchService.findByExternalId(fdMatch.id()).orElse(new Match());
        fdMatchConverter.mapMatchFromTo(fdMatch, match);
        return match;
    }
}

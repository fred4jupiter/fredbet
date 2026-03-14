package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.admin.CacheAdministrationService;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.integration.model.FdMatch;
import de.fred4jupiter.fredbet.match.MatchRepository;
import de.fred4jupiter.fredbet.match.MatchService;
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

    private final MatchRepository matchRepository;

    private final CacheAdministrationService cacheAdministrationService;

    FootballDataSyncService(FootballDataRestClient footballDataRestClient,
                            MatchService matchService, FdMatchConverter fdMatchConverter, MatchRepository matchRepository, CacheAdministrationService cacheAdministrationService) {
        this.footballDataRestClient = footballDataRestClient;
        this.matchService = matchService;
        this.fdMatchConverter = fdMatchConverter;
        this.matchRepository = matchRepository;
        this.cacheAdministrationService = cacheAdministrationService;
    }

    public int syncData(String competitionCode, int seasonYear) {
        final List<FdMatch> matches = footballDataRestClient.fetchMatches(competitionCode, seasonYear);
        if (matches == null || matches.isEmpty()) {
            LOG.warn("Could not load football data fdMatchesList!");
            return 0;
        }

        LOG.info("fetched {} matches.", matches.size());
        final List<Match> updatedMatches = matches.stream().map(fdMatch -> {
                final Match match = matchRepository.findByExternalId(fdMatch.id()).orElse(new Match());
                fdMatchConverter.mapMatchFromTo(fdMatch, match);
                return match;
            })
            .filter(match -> match.getExternalId() != null)
            .toList();

        matchRepository.saveAll(updatedMatches);
        LOG.info("saved {} matches.", updatedMatches.size());
        cacheAdministrationService.clearCaches();
        return updatedMatches.size();
    }
}

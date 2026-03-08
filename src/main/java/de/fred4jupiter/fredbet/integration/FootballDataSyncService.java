package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.integration.model.FdMatch;
import de.fred4jupiter.fredbet.match.MatchRepository;
import de.fred4jupiter.fredbet.match.MatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class FootballDataSyncService {

    private static final Logger LOG = LoggerFactory.getLogger(FootballDataSyncService.class);

    private final FootballDataRestClient footballDataRestClient;

    private final MatchService matchService;

    private final FdMatchConverter fdMatchConverter;

    private final MatchRepository matchRepository;

    FootballDataSyncService(FootballDataRestClient footballDataRestClient,
                            MatchService matchService, FdMatchConverter fdMatchConverter, MatchRepository matchRepository) {
        this.footballDataRestClient = footballDataRestClient;
        this.matchService = matchService;
        this.fdMatchConverter = fdMatchConverter;
        this.matchRepository = matchRepository;
    }

    public int syncData(String competitionCode, int seasonYear) {
        List<FdMatch> matches = footballDataRestClient.fetchMatches(competitionCode, seasonYear);
        if (matches == null || matches.isEmpty()) {
            LOG.warn("Could not load football data fdMatchesList!");
            return 0;
        }

        LOG.info("fetched {} matches.", matches.size());

        matches.forEach(this::syncMatch);

        return matchService.countMatches().intValue();
    }

    private void syncMatch(FdMatch fdMatch) {
        Optional<Match> matchOptional = matchRepository.findByExternalId(fdMatch.id());
        if (matchOptional.isPresent()) {
            Match match = matchOptional.get();
            fdMatchConverter.updateMatchFromFdMatch(fdMatch, match);
            matchRepository.save(match);
        } else {
            Match match = fdMatchConverter.mapToMatch(fdMatch);
            if (match != null) {
                matchRepository.save(match);
            }
        }
    }

    private int syncData(List<FdMatch> fdMatches) {
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

package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.admin.CacheAdministrationService;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.integration.model.FdFullTime;
import de.fred4jupiter.fredbet.integration.model.FdMatch;
import de.fred4jupiter.fredbet.integration.model.FdMatches;
import de.fred4jupiter.fredbet.integration.model.FdScore;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.props.CacheNames;
import de.fred4jupiter.fredbet.util.JsonObjectConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class FootballDataSyncService {

    private static final Logger LOG = LoggerFactory.getLogger(FootballDataSyncService.class);

    private final FootballDataRestClient footballDataRestClient;

    private final MatchService matchService;

    private final FdMatchSyncImporter fdMatchSyncImporter;

    private final CacheAdministrationService administrationService;

    private final JsonObjectConverter jsonObjectConverter;

    FootballDataSyncService(FootballDataRestClient footballDataRestClient,
                            MatchService matchService, FdMatchSyncImporter fdMatchSyncImporter, CacheAdministrationService administrationService, JsonObjectConverter jsonObjectConverter) {
        this.footballDataRestClient = footballDataRestClient;
        this.matchService = matchService;
        this.fdMatchSyncImporter = fdMatchSyncImporter;
        this.administrationService = administrationService;
        this.jsonObjectConverter = jsonObjectConverter;
    }

    public void syncData(String competitionCode, int seasonYear) {
        syncData(footballDataRestClient.fetchMatches(competitionCode, seasonYear));
    }

    public void syncDataFromJson(byte[] fsMatchesAsJson, boolean removeResults) {
        FdMatches fdMatches = jsonObjectConverter.fromJson(new String(fsMatchesAsJson), FdMatches.class);
        if (fdMatches == null || fdMatches.matches().isEmpty()) {
            LOG.warn("Could not load football data fdMatches from json!");
            return;
        }

        List<FdMatch> matches = fdMatches.matches();
        List<FdMatch> updatedMatches = matches.stream()
            .map(fdMatch -> {
                Integer goalsHome = removeResults ? null : fdMatch.score().fullTime().home();
                Integer goalsAway = removeResults ? null : fdMatch.score().fullTime().away();
                FdScore score = new FdScore(new FdFullTime(goalsHome, goalsAway));
                return new FdMatch(fdMatch.id(), fdMatch.homeTeam(), fdMatch.awayTeam(), fdMatch.group(), fdMatch.utcDate().plusYears(1), score,
                    ZonedDateTime.now(), fdMatch.stage(), fdMatch.status(), fdMatch.venue());
            })
            .toList();

        syncData(updatedMatches);
    }

    public void syncData(List<FdMatch> matches) {
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

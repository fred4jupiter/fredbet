package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.integration.model.*;
import de.fred4jupiter.fredbet.match.MatchGoalsChangedEvent;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.settings.RuntimeSettings;
import de.fred4jupiter.fredbet.settings.RuntimeSettingsService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
class FdMatchSyncImporter {

    private static final Logger LOG = LoggerFactory.getLogger(FdMatchSyncImporter.class);

    private final RuntimeSettingsService runtimeSettingsService;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final MatchService matchService;

    private final TeamNameToCountryResolver teamNameToCountryResolver;

    private final CrestsDownloader crestsDownloader;

    private final CrestsCountryResolver crestsCountryResolver;

    FdMatchSyncImporter(RuntimeSettingsService runtimeSettingsService, ApplicationEventPublisher applicationEventPublisher, MatchService matchService,
                        TeamNameToCountryResolver teamNameToCountryResolver, CrestsDownloader crestsDownloader, CrestsCountryResolver crestsCountryResolver) {
        this.runtimeSettingsService = runtimeSettingsService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.matchService = matchService;
        this.teamNameToCountryResolver = teamNameToCountryResolver;
        this.crestsDownloader = crestsDownloader;
        this.crestsCountryResolver = crestsCountryResolver;
    }

    public void mapAndSave(FdMatch fdMatch, Match match, boolean forceUpdate) {
        if (fdMatch == null || fdMatch.homeTeam() == null || fdMatch.awayTeam() == null) {
            LOG.debug("match is null or home/away team is null for match {}", fdMatch);
            return;
        }

        if (match.getExternalId() != null && !fdMatch.isUpdatedAfter(match.getExternalLastUpdated()) && !forceUpdate) {
            LOG.debug("match with id={} is already up to date. No update needed. lastUpdate fdMatch={}, lastUpdate match={}", fdMatch.id(), fdMatch.lastUpdated(), match.getExternalLastUpdated());
            return;
        }

        LOG.info("updates will be applied for fdMatch={}", fdMatch);

        mapTeam(fdMatch.homeTeam(), match.getTeamOne());
        mapTeam(fdMatch.awayTeam(), match.getTeamTwo());

        final Group group = resolveToGroup(fdMatch);
        if (group == null) {
            LOG.warn("No group found for match {}", fdMatch);
            return;
        }

        match.setGroup(group);
        match.setKickOffDate(convertToLocalDateTime(fdMatch.utcDate()));
        match.setExternalId(fdMatch.id());
        match.setExternalLastUpdated(fdMatch.lastUpdated());
        match.setStadium(fdMatch.venue());

        if (!fdMatch.isFinished()) {
            matchService.save(match);
            return;
        }

        // update results
        mapGoals(fdMatch, match);
    }

    private void mapGoals(FdMatch fdMatch, Match match) {
        final FdScore score = fdMatch.score();
        if (score == null) {
            LOG.warn("No score set in fdMatch={}", fdMatch);
            return;
        }

        if (match.hasResultSet()) {
            LOG.warn("Saved match has result already set. Will not update the goals setting. match={}", match);
            return;
        }

        if (FdScoreDuration.PENALTY_SHOOTOUT.equals(score.duration())) {
            match.setGoalsTeamOne(score.regularTime().home());
            match.setGoalsTeamTwo(score.regularTime().away());
            match.setPenaltyWinnerOne(FdScoreWinner.HOME_TEAM.equals(score.winner()));
        } else {
            match.setGoalsTeamOne(score.fullTime().home());
            match.setGoalsTeamTwo(score.fullTime().away());
        }

        Match saved = matchService.save(match);
        applicationEventPublisher.publishEvent(new MatchGoalsChangedEvent(saved));
    }

    private void mapTeam(FdTeam fdTeam, Team team) {
        final Country country = teamNameToCountryResolver.resolveToCountry(fdTeam);
        if (country != null) {
            team.setCountry(country);
            team.setName(null);
            crestsCountryResolver.loadCrestsImageFor(country).ifPresent(team::setCrestsBinary);
        } else {
            team.setName(StringUtils.isNotBlank(fdTeam.name()) ? fdTeam.name() : "Not yet defined");
        }

        if (team.getCrestsBinary() == null) {
            crestsDownloader.downloadCrestsByUrl(fdTeam.id()).ifPresent(team::setCrestsBinary);
        }
    }

    private Group resolveToGroup(FdMatch fdMatch) {
        if (FdMatchStage.GROUP_STAGE.equals(fdMatch.stage())) {
            return Group.valueOf(fdMatch.group().name());
        }

        return switch (fdMatch.stage()) {
            case LAST_32 -> Group.ROUND_OF_THIRTY_TWO;
            case LAST_16 -> Group.ROUND_OF_SIXTEEN;
            case QUARTER_FINALS -> Group.QUARTER_FINAL;
            case SEMI_FINALS -> Group.SEMI_FINAL;
            case FINAL -> Group.FINAL;
            case THIRD_PLACE -> Group.GAME_FOR_THIRD;
            default -> null;
        };
    }

    private LocalDateTime convertToLocalDateTime(ZonedDateTime utcZoned) {
        RuntimeSettings runtimeSettings = runtimeSettingsService.loadRuntimeSettings();
        ZoneId zoneId = ZoneId.of(runtimeSettings.getTimeZone());
        ZonedDateTime convertedAsZoneDateTime = utcZoned.withZoneSameInstant(zoneId);
        return convertedAsZoneDateTime.toLocalDateTime();
    }
}

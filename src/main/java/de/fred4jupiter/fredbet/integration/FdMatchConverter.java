package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.integration.model.FdFullTime;
import de.fred4jupiter.fredbet.integration.model.FdMatch;
import de.fred4jupiter.fredbet.integration.model.FdTeam;
import de.fred4jupiter.fredbet.match.MatchGoalsChangedEvent;
import de.fred4jupiter.fredbet.match.MatchRepository;
import de.fred4jupiter.fredbet.settings.RuntimeSettings;
import de.fred4jupiter.fredbet.settings.RuntimeSettingsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Component
class FdMatchConverter {

    private static final Logger LOG = LoggerFactory.getLogger(FdMatchConverter.class);

    private final RuntimeSettingsService runtimeSettingsService;

    private final Properties countryProps;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final MatchRepository matchRepository;

    FdMatchConverter(@Value("classpath:/msgs/TeamKey_en.properties") Resource countryNameResource,
                     RuntimeSettingsService runtimeSettingsService, ApplicationEventPublisher applicationEventPublisher, MatchRepository matchRepository) {
        this.runtimeSettingsService = runtimeSettingsService;
        this.countryProps = loadCountryNames(countryNameResource);
        this.applicationEventPublisher = applicationEventPublisher;
        this.matchRepository = matchRepository;
    }

    public void mapAndSave(FdMatch fdMatch, Match match) {
        if (fdMatch == null || fdMatch.homeTeam() == null || fdMatch.awayTeam() == null) {
            LOG.warn("match is null or home/away team is null for match {}", fdMatch);
            return;
        }

        if (match.getExternalId() != null && !fdMatch.isUpdatedAfter(match.getExternalLastUpdated())) {
            LOG.info("match with id={} is already up to date. No update needed. lastUpdate fdMatch={}, lastUpdate match={}", fdMatch.id(), fdMatch.lastUpdated(), match.getExternalLastUpdated());
            return;
        }

        LOG.debug("start syncing fdMatch={}", fdMatch);

        mapTeam(fdMatch.homeTeam(), match.getTeamOne());
        mapTeam(fdMatch.awayTeam(), match.getTeamTwo());

        Group group = resolveToGroup(fdMatch);
        if (group == null) {
            LOG.warn("No group found for match {}", fdMatch);
            return;
        }

        match.setGroup(group);
        match.setKickOffDate(convertToLocalDateTime(fdMatch.utcDate()));
        match.setExternalId(fdMatch.id());
        match.setExternalLastUpdated(fdMatch.lastUpdated());

        // update results
        if (fdMatch.score() != null && fdMatch.score().fullTime() != null && fdMatch.isFinished()) {
            FdFullTime fdFullTime = fdMatch.score().fullTime();
            if (!match.hasResultSet() && fdMatch.isFinished()) {
                match.setGoalsTeamOne(fdFullTime.home());
                match.setGoalsTeamTwo(fdFullTime.away());
                LOG.debug("saved result for match={}", match);
            }
        }

        Match saved = matchRepository.save(match);
        applicationEventPublisher.publishEvent(new MatchGoalsChangedEvent(saved));

        LOG.debug("finished syncing fdMatch={}", fdMatch);
    }

    private void mapTeam(FdTeam fdTeam, Team team) {
        final Country country = resolveToCountry(fdTeam, countryProps);
        if (country != null) {
            team.setCountry(country);
        } else {
            team.setName(StringUtils.isNotBlank(fdTeam.name()) ? fdTeam.name() : "Not yet defined");
        }
    }

    private Group resolveToGroup(FdMatch fdMatch) {
        if ("GROUP_STAGE".equals(fdMatch.stage())) {
            return Group.valueOf(fdMatch.group());
        }

        return switch (fdMatch.stage()) {
            case "LAST_32" -> Group.ROUND_OF_THIRTY_TWO;
            case "LAST_16" -> Group.ROUND_OF_SIXTEEN;
            case "QUARTER_FINALS" -> Group.QUARTER_FINAL;
            case "SEMI_FINALS" -> Group.SEMI_FINAL;
            case "FINAL" -> Group.FINAL;
            case "THIRD_PLACE" -> Group.GAME_FOR_THIRD;
            default -> null;
        };
    }

    private LocalDateTime convertToLocalDateTime(ZonedDateTime utcZoned) {
        RuntimeSettings runtimeSettings = runtimeSettingsService.loadRuntimeSettings();
        ZoneId zoneId = ZoneId.of(runtimeSettings.getTimeZone());
        ZonedDateTime convertedAsZoneDateTime = utcZoned.withZoneSameInstant(zoneId);
        return convertedAsZoneDateTime.toLocalDateTime();
    }

    private Country resolveToCountry(FdTeam team, Properties countryProps) {
        if (team == null || team.tla() == null || team.name() == null) {
            return null;
        }

        Country country = Country.fromAlpha3Code(team.tla().toLowerCase());
        if (country != null) {
            return country;
        }

        if (countryProps.containsValue(team.name())) {
            String key = getKeyByValue(countryProps, team.name());
            if (StringUtils.isNotBlank(key)) {
                String alpha3IsoCode = Strings.CS.remove(key, "country.");
                return Country.fromAlpha3Code(alpha3IsoCode);
            }
        }

        LOG.warn("Could not resolve country for team '{}'.", team);
        return null;
    }

    private String getKeyByValue(Properties countryProps, String name) {
        Set<Map.Entry<Object, Object>> entries = countryProps.entrySet();

        for (Map.Entry<Object, Object> next : entries) {
            if (next.getValue().equals(name)) {
                return (String) next.getKey();
            }
        }

        return null;
    }

    private Properties loadCountryNames(Resource countryNameResource) {
        try (final InputStream in = countryNameResource.getInputStream()) {
            final Properties properties = new Properties();
            properties.load(in);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}

package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.builder.MatchBuilder;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.integration.model.FdFullTime;
import de.fred4jupiter.fredbet.integration.model.FdMatch;
import de.fred4jupiter.fredbet.integration.model.FdTeam;
import de.fred4jupiter.fredbet.settings.RuntimeSettings;
import de.fred4jupiter.fredbet.settings.RuntimeSettingsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    private final Resource countryNameResource;

    private final RuntimeSettingsService runtimeSettingsService;

    private final Properties countryProps;

    FdMatchConverter(@Value("classpath:/msgs/TeamKey_en.properties") Resource countryNameResource, RuntimeSettingsService runtimeSettingsService) {
        this.countryNameResource = countryNameResource;
        this.runtimeSettingsService = runtimeSettingsService;
        this.countryProps = loadCountryNames();
    }

    public Match mapToMatch(FdMatch fdMatch) {
        if (fdMatch == null || fdMatch.homeTeam() == null || fdMatch.awayTeam() == null) {
            LOG.warn("match is null or home/away team is null for match {}", fdMatch);
            return null;
        }

        if (fdMatch.homeTeam().id() == null && fdMatch.awayTeam().id() == null) {
            LOG.warn("match has no team ids for match {}", fdMatch);
            return null;
        }

        final MatchBuilder matchBuilder = MatchBuilder.create();

        final Country teamOneCountry = resolveToCountry(fdMatch.homeTeam(), countryProps);
        if (teamOneCountry != null) {
            matchBuilder.withTeamOne(teamOneCountry);
        } else {
            matchBuilder.withTeamOne(StringUtils.isNotBlank(fdMatch.homeTeam().name()) ? fdMatch.homeTeam().name() : "Not yet defined");
        }

        final Country teamTwoCountry = resolveToCountry(fdMatch.awayTeam(), countryProps);
        if (teamTwoCountry != null) {
            matchBuilder.withTeamTwo(teamTwoCountry);
        } else {
            matchBuilder.withTeamTwo(StringUtils.isNotBlank(fdMatch.awayTeam().name()) ? fdMatch.awayTeam().name() : "Not yet defined");
        }

        Group group = resolveToGroup(fdMatch);
        if (group == null) {
            LOG.warn("No group found for match {}", fdMatch);
            return null;
        }

        matchBuilder.withGroup(group);

        matchBuilder.withKickOffDate(convertToLocalDateTime(fdMatch.utcDate()));

        // update results
        if (fdMatch.score() != null && fdMatch.score().fullTime() != null) {
            FdFullTime fdFullTime = fdMatch.score().fullTime();
            matchBuilder.withGoals(fdFullTime.home(), fdFullTime.away());
        }
        Match match = matchBuilder.build();
        match.setExternalId(fdMatch.id());
        return match;
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

    private Properties loadCountryNames() {
        try (final InputStream in = countryNameResource.getInputStream()) {
            final Properties properties = new Properties();
            properties.load(in);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}

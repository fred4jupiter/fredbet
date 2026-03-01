package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.builder.MatchBuilder;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.integration.model.FdMatch;
import de.fred4jupiter.fredbet.integration.model.FdTeam;
import de.fred4jupiter.fredbet.settings.RuntimeSettings;
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

    FdMatchConverter(@Value("classpath:/msgs/TeamKey_en.properties") Resource countryNameResource) {
        this.countryNameResource = countryNameResource;
    }

    public Match mapToMatch(FdMatch fdMatch, RuntimeSettings runtimeSettings) {
        if (fdMatch == null || fdMatch.homeTeam() == null || fdMatch.awayTeam() == null) {
            return null;
        }

        if (fdMatch.homeTeam().id() == null && fdMatch.awayTeam().id() == null) {
            return null;
        }

        final Properties countryProps = loadCountryNames();

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

        matchBuilder.withGroup(resolveToGroup(fdMatch.group()));

        matchBuilder
            .withKickOffDate(convertToLocalDateTime(fdMatch.utcDate(), runtimeSettings))
            .withStadium(fdMatch.venue());

        // update results
        if (fdMatch.score() != null && fdMatch.score().fullTime() != null) {
            matchBuilder.withGoals(fdMatch.score().fullTime().home(), fdMatch.score().fullTime().away());
        }
        return matchBuilder.build();
    }

    private Group resolveToGroup(String groupName) {
        if (StringUtils.isBlank(groupName)) {
            return null;
        }

        try {
            return Group.valueOf(groupName);
        } catch (IllegalArgumentException e) {
            LOG.warn("Could not resolve group for group name '{}'.", groupName);
            return null;
        }
    }

    private LocalDateTime convertToLocalDateTime(ZonedDateTime utcZoned, RuntimeSettings runtimeSettings) {
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

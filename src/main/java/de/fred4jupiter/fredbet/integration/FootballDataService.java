package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.betting.BettingService;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.builder.MatchBuilder;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.integration.model.FdMatch;
import de.fred4jupiter.fredbet.integration.model.FdMatches;
import de.fred4jupiter.fredbet.integration.model.FdTeam;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.props.FootballDataProperties;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.settings.RuntimeSettings;
import de.fred4jupiter.fredbet.settings.RuntimeSettingsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Service
public class FootballDataService {

    private static final Logger LOG = LoggerFactory.getLogger(FootballDataService.class);

    private final FredbetProperties fredbetProperties;

    private final MatchService matchService;

    private final BettingService bettingService;

    private final RuntimeSettingsService runtimeSettingsService;

    public FootballDataService(FredbetProperties fredbetProperties, MatchService matchService, BettingService bettingService,
                               RuntimeSettingsService runtimeSettingsService) {
        this.fredbetProperties = fredbetProperties;
        this.matchService = matchService;
        this.bettingService = bettingService;
        this.runtimeSettingsService = runtimeSettingsService;
    }

    public void importData() {
        final FootballDataProperties footballDataProperties = this.fredbetProperties.integration().footballData();
        RestClient restClient = RestClient.builder().baseUrl(footballDataProperties.baseUrl()).defaultHeader("X-Auth-Token", footballDataProperties.apiToken()).build();
        FdMatches fdMatches = restClient.get().uri("/competitions/WC/matches?season=2026").attribute("season", 2026).retrieve().body(FdMatches.class);
        LOG.debug("Response from Football Data: fdMatches={}", fdMatches);
        importMatches(fdMatches);
    }

    private void importMatches(FdMatches fdMatches) {
        if (fdMatches == null) {
            LOG.warn("Could not load football data matches!");
            return;
        }
        LOG.debug("Importing {} Football Data matches", fdMatches.matches().size());

        bettingService.deleteAllBets();
        LOG.info("deleted all bets");

        matchService.deleteAllMatches();
        LOG.info("deleted all matches");

        List<FdMatch> matches = fdMatches.matches();

        Properties countryProps = loadCountryNames();

        RuntimeSettings runtimeSettings = runtimeSettingsService.loadRuntimeSettings();

        matches.forEach(fdMatch -> {
            Match match = mapToMatch(fdMatch, countryProps, runtimeSettings);
            if (match != null) {
                matchService.save(match);
            }
        });
        LOG.debug("imported {} matches", matches.size());
    }

    private Match mapToMatch(FdMatch fdMatch, Properties countryProps, RuntimeSettings runtimeSettings) {
        if (fdMatch == null || fdMatch.homeTeam() == null || fdMatch.homeTeam().name() == null || fdMatch.awayTeam() == null || fdMatch.awayTeam().name() == null) {
            return null;
        }

        final MatchBuilder matchBuilder = MatchBuilder.create();

        final Country teamOneCountry = resolveToCountry(fdMatch.homeTeam(), countryProps);
        if (teamOneCountry != null) {
            matchBuilder.withTeamOne(teamOneCountry);
        } else {
            matchBuilder.withTeamOne(fdMatch.homeTeam().name());
        }

        final Country teamTwoCountry = resolveToCountry(fdMatch.awayTeam(), countryProps);
        if (teamTwoCountry != null) {
            matchBuilder.withTeamTwo(teamTwoCountry);
        } else {
            matchBuilder.withTeamTwo(fdMatch.awayTeam().name());
        }

        String groupName = fdMatch.group();
        try {
            if (StringUtils.isNotBlank(groupName)) {
                Group group = Group.valueOf(groupName);
                matchBuilder.withGroup(group);
            } else {
                LOG.warn("No group name for match {} vs {}. Defaulting to GROUP_A", fdMatch.homeTeam().name(), fdMatch.awayTeam().name());
                matchBuilder.withGroup(Group.GROUP_A);
            }
        } catch (IllegalArgumentException e) {
            LOG.error("Invalid group name '{}' for match {} vs {}. Defaulting to GROUP_A", groupName, fdMatch.homeTeam().name(), fdMatch.awayTeam().name());
            matchBuilder.withGroup(Group.GROUP_A);
        }

        matchBuilder
            .withKickOffDate(convertToLocalDateTime(fdMatch.utcDate(), runtimeSettings))
            .withStadium(fdMatch.venue());
        return matchBuilder.build();
    }

    private LocalDateTime convertToLocalDateTime(ZonedDateTime utcZoned, RuntimeSettings runtimeSettings) {
        ZoneId zoneId = ZoneId.of(runtimeSettings.getTimeZone());
        ZonedDateTime convertedAsZoneDateTime = utcZoned.withZoneSameInstant(zoneId);
        return convertedAsZoneDateTime.toLocalDateTime();
    }

    private Country resolveToCountry(FdTeam fdTeam, Properties countryProps) {
        if (StringUtils.isNotBlank(fdTeam.tla())) {
            Country country = Country.fromAlpha3Code(fdTeam.tla().toLowerCase());
            if (country != null) {
                return country;
            }
        }

        if (countryProps.containsValue(fdTeam.name())) {
            String key = getKeyByValue(countryProps, fdTeam.name());
            if (StringUtils.isNotBlank(key)) {
                String alpha3IsoCode = Strings.CS.remove(key, "country.");
                return Country.fromAlpha3Code(alpha3IsoCode);
            }
        }

        LOG.warn("Could not resolve country for team '{}'.", fdTeam);

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
        final Properties properties = new Properties();

        ClassPathResource classPathResource = new ClassPathResource("/msgs/TeamKey_en.properties");
        try (final InputStream in = classPathResource.getInputStream()) {
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }
}

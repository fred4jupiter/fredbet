package de.fred4jupiter.fredbet.web.util;

import com.neovisionaries.i18n.CountryCode;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.match.MatchRepository;
import de.fred4jupiter.fredbet.settings.RuntimeSettingsService;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
public class TeamUtil {

    private static final Logger LOG = LoggerFactory.getLogger(TeamUtil.class);

    private final MessageSourceUtil messageSourceUtil;

    private final Map<Country, String> alpha2Codes = new HashMap<>();

    private final RuntimeSettingsService runtimeSettingsService;

    private final MatchRepository matchRepository;

    public TeamUtil(MessageSourceUtil messageSourceUtil, RuntimeSettingsService runtimeSettingsService, MatchRepository matchRepository) {
        this.messageSourceUtil = messageSourceUtil;
        this.runtimeSettingsService = runtimeSettingsService;
        this.matchRepository = matchRepository;

        List<Country> countryList = Stream.of(Country.values()).toList();
        countryList.forEach(country -> {
            this.alpha2Codes.put(country, resolveFor(country));
        });
    }

    private String resolveFor(Country country) {
        if (StringUtils.isNotBlank(country.getFlagIconCode())) {
            return country.getFlagIconCode();
        }

        if (StringUtils.isNotBlank(country.getAlpha3Code())) {
            String alpha3 = country.getAlpha3Code().toUpperCase();
            CountryCode countryCode = CountryCode.getByAlpha3Code(alpha3);
            if (countryCode != null && countryCode.getAlpha2() != null) {
                return countryCode.getAlpha2();
            }
            LOG.warn("missing alpha 2 code for country={}, alpha3={}", country, alpha3);
        }

        return null;
    }

    public String i18n(Country country) {
        if (country == null) {
            return "";
        }
        if (StringUtils.isNotBlank(country.getAlpha3Code())) {
            return getMessageFor("country." + country.getAlpha3Code());
        }

        return getMessageFor("team." + country.name());
    }

    private String getMessageFor(String key) {
        return messageSourceUtil.getMessageFor(key, LocaleContextHolder.getLocale());
    }

    public String cssClassFor(Country country) {
        if (country == null) {
            return "";
        }

        if (StringUtils.isNotBlank(country.getCssIconClass())) {
            return country.getCssIconClass();
        }

        return cssClassFor(this.alpha2Codes.get(country));
    }

    public String cssClassFor(String alpha2Code) {
        return "fi fi-%s".formatted(alpha2Code.toLowerCase());
    }

    public List<TeamView> getAvailableTeams() {
        List<Country> allPossibleCountries = runtimeSettingsService.loadRuntimeSettings().getTeamBundle().getTeams();
        return toListOfTeamViews(allPossibleCountries);
    }

    public List<TeamView> getAvailableTeamsBasedOnMatches() {
        List<Country> allPossibleCountries = matchRepository.getAllCountriesOfMatches();
        return toListOfTeamViews(allPossibleCountries);
    }

    private List<TeamView> toListOfTeamViews(List<Country> countries) {
        return countries.stream()
            .map(country -> new TeamView(country, i18n(country)))
            .sorted(Comparator.comparing(TeamView::teamName))
            .toList();
    }
}

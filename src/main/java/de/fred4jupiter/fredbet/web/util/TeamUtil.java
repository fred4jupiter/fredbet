package de.fred4jupiter.fredbet.web.util;

import de.fred4jupiter.fredbet.country.CountryResolver;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.match.MatchRepository;
import de.fred4jupiter.fredbet.teambundle.TeamBundleProvider;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class TeamUtil {

    private final MessageSourceUtil messageSourceUtil;

    private final MatchRepository matchRepository;

    private final CountryResolver countryAlpha2Codes;

    public TeamUtil(MessageSourceUtil messageSourceUtil, MatchRepository matchRepository, TeamBundleProvider teamBundleProvider,
                    CountryResolver countryAlpha2Codes) {
        this.messageSourceUtil = messageSourceUtil;
        this.matchRepository = matchRepository;
        this.countryAlpha2Codes = countryAlpha2Codes;
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

        String alpha2Code = countryAlpha2Codes.countryToAlpha2Code(country);
        return cssClassFor(alpha2Code);
    }

    public String cssClassFor(String alpha2Code) {
        if (StringUtils.isBlank(alpha2Code)) {
            return "";
        }
        return "fi fi-%s".formatted(alpha2Code.toLowerCase());
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

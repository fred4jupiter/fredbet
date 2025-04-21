package de.fred4jupiter.fredbet.country;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.match.MatchRepository;
import de.fred4jupiter.fredbet.settings.RuntimeSettingsService;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CountryService {

    private final MessageSourceUtil messageSourceUtil;

    private final MatchRepository matchRepository;

    private final RuntimeSettingsService runtimeSettingsService;

    public CountryService(MessageSourceUtil messageSourceUtil, MatchRepository matchRepository, RuntimeSettingsService runtimeSettingsService) {
        this.messageSourceUtil = messageSourceUtil;
        this.matchRepository = matchRepository;
        this.runtimeSettingsService = runtimeSettingsService;
    }

    public List<Country> getAvailableCountriesBasedOnMatches(Locale locale) {
        return translateAndSort(locale, getAllCountriesOfMatches());
    }

    public List<Country> getAllCountriesOfMatches() {
        List<Country[]> allCountries = matchRepository.findAllCountriesOfMatches();
        return allCountries.stream().flatMap(Stream::of).distinct().toList();
    }

    private List<Country> translateAndSort(Locale locale, List<Country> countries) {
        return countries.stream().filter(Objects::nonNull)
            .sorted(Comparator.comparing((Country country) -> messageSourceUtil.getCountryName(country, locale)))
            .toList();
    }
}

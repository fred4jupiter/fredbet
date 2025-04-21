package de.fred4jupiter.fredbet.country;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.match.MatchRepository;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class CountryService {

    private final MessageSourceUtil messageSourceUtil;

    private final MatchRepository matchRepository;

    public CountryService(MessageSourceUtil messageSourceUtil, MatchRepository matchRepository) {
        this.messageSourceUtil = messageSourceUtil;
        this.matchRepository = matchRepository;
    }

    public List<Country> getAvailableCountriesBasedOnMatches(Locale locale) {
        return translateAndSort(locale, getAllCountriesOfMatches());
    }

    private List<Country> getAllCountriesOfMatches() {
        List<Country[]> allCountries = matchRepository.findAllCountriesOfMatches();
        return allCountries.stream().flatMap(Stream::of).distinct().toList();
    }

    private List<Country> translateAndSort(Locale locale, List<Country> countries) {
        return countries.stream().filter(Objects::nonNull)
            .sorted(Comparator.comparing((Country country) -> messageSourceUtil.getCountryName(country, locale)))
            .toList();
    }
}

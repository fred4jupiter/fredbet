package de.fred4jupiter.fredbet.country;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.match.MatchRepository;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CountryService {

    private final MessageSourceUtil messageSourceUtil;

    private final MatchRepository matchRepository;

    public CountryService(MessageSourceUtil messageSourceUtil, MatchRepository matchRepository) {
        this.messageSourceUtil = messageSourceUtil;
        this.matchRepository = matchRepository;
    }

    public List<Country> getAvailableCountriesSortedWithNoneEntryByLocale(Locale locale, Group group) {
        final LinkedList<Country> result = new LinkedList<>();

        if (group == null || !group.isKnockoutRound()) {
            result.addAll(sortCountries(locale, getAllCountriesWithoutNoneEntry()));
        } else {
            Set<Country> countries = getAvailableCountriesWithoutNoneEntry();
            List<Country> sortedCountries = sortCountries(locale, new ArrayList<>(countries));
            result.addAll(sortedCountries);
        }

        result.addFirst(Country.NONE);
        return result;
    }

    /*
     * show in extra bets and in runtime config
     */
    public List<Country> getAvailableCountriesExtraBetsSortedWithNoneEntryByLocale(Locale locale) {
        final Set<Country> resultset = getAvailableCountriesWithoutNoneEntry();
        List<Country> sortCountries = sortCountries(locale, new ArrayList<>(resultset));

        LinkedList<Country> result = new LinkedList<>(sortCountries);
        if (!result.contains(Country.NONE)) {
            result.addFirst(Country.NONE);
        }
        return result;
    }

    /*
     * for random extra bets
     */
    public Set<Country> getAvailableCountriesWithoutNoneEntry() {
        List<Country[]> allCountries = matchRepository.findAllCountries();
        return allCountries.stream().flatMap(Stream::of).collect(Collectors.toSet());
    }

    /*
     * for random matches
     */
    public List<Country> getAllCountriesWithoutNoneEntry() {
        return Arrays.stream(Country.values()).filter(country -> !country.equals(Country.NONE)).toList();
    }

    private List<Country> sortCountries(Locale locale, List<Country> countriesWithoutNoneEntry) {
        return countriesWithoutNoneEntry.stream().filter(Objects::nonNull)
                .sorted(Comparator.comparing((Country country) -> messageSourceUtil.getCountryName(country, locale)))
                .toList();
    }
}

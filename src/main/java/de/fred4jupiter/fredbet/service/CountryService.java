package de.fred4jupiter.fredbet.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.repository.MatchRepository;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;

@Service
public class CountryService {

    @Autowired
    private MessageSourceUtil messageSourceUtil;

    @Autowired
    private MatchRepository matchRepository;

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
     * to show in runtime config
     */
    public List<Country> getAvailableCountriesSortedWithoutNoneEntry(Locale locale) {
        List<Country> countriesWithoutNoneEntry = getAllCountriesWithoutNoneEntry();
        return sortCountries(locale, countriesWithoutNoneEntry);
    }

    /*
     * show in extra bets
     */
    public List<Country> getAvailableCountriesExtraBetsSortedWithNoneEntryByLocale(Locale locale) {
        final Set<Country> resultset = getAvailableCountriesWithoutNoneEntry();
        List<Country> sortCountries = sortCountries(locale, new ArrayList<>(resultset));

        LinkedList<Country> result = new LinkedList<>(sortCountries);
        result.addFirst(Country.NONE);
        return result;
    }

    /*
     * for random extra bets
     */
    public Set<Country> getAvailableCountriesWithoutNoneEntry() {
        List<Match> allMatches = matchRepository.findAll();
        return toCountrySet(allMatches);
    }

    /*
     * for random matches
     */
    public List<Country> getAllCountriesWithoutNoneEntry() {
        return Arrays.stream(Country.values()).filter(country -> !country.equals(Country.NONE)).collect(Collectors.toList());
    }

    private List<Country> sortCountries(Locale locale, List<Country> countriesWithoutNoneEntry) {
        return countriesWithoutNoneEntry.stream().filter(Objects::nonNull)
                .sorted(Comparator.comparing((Country country) -> messageSourceUtil.getCountryName(country, locale)))
                .collect(Collectors.toList());
    }

    private Set<Country> toCountrySet(List<Match> matches) {
        final Set<Country> resultset = new HashSet<>();
        matches.stream().filter(match -> match != null && (match.getCountryOne() != null || match.getCountryTwo() != null))
                .forEach(match -> {
                    resultset.add(match.getCountryOne());
                    resultset.add(match.getCountryTwo());
                });
        return resultset;
    }

}

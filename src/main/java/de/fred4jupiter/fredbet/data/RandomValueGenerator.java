package de.fred4jupiter.fredbet.data;

import de.fred4jupiter.fredbet.country.CountryService;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.teambundle.TeamBundle;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class RandomValueGenerator {

    private final CountryService countryService;

    public RandomValueGenerator(CountryService countryService) {
        this.countryService = countryService;
    }

    public Integer generateRandomValue() {
        return generateRandomValueInRange(1, 5);
    }

    public Integer generateRandomValueInRange(int min, int max) {
        Random rn = new Random();
        int range = max - min + 1;
        return rn.nextInt(range) + min;
    }

    public Boolean generateRandomBoolean() {
        return Math.random() < 0.5;
    }

    private Country generateRandomCountry(List<Country> availableCountries) {
        Integer randomVal = generateRandomValueInRange(0, availableCountries.size() - 1);
        return availableCountries.get(randomVal);
    }

    public ImmutablePair<Country, Country> generateTeamPair(TeamBundle teamBundle) {
        return generateTeamPair(teamBundle.getTeams());
    }

    public ImmutablePair<Country, Country> generateTeamPair(List<Country> availCountries) {
        if (CollectionUtils.isEmpty(availCountries)) {
            return null;
        }

        Country countryOne = generateRandomCountry(availCountries);
        List<Country> resultList = availCountries.stream().filter(country -> !country.equals(countryOne)).toList();

        if (CollectionUtils.isEmpty(resultList)) {
            return ImmutablePair.of(countryOne, countryOne);
        }

        Country countryTwo = generateRandomCountry(resultList);
        return ImmutablePair.of(countryOne, countryTwo);
    }

    public ImmutableTriple<Country, Country, Country> generateTeamTriple() {
        final List<Country> availCountries = new ArrayList<>(countryService.getAllCountriesOfMatches());
        if (CollectionUtils.isEmpty(countryService.getAllCountriesOfMatches())) {
            return null;
        }

        Country countryOne = generateRandomCountry(availCountries);
        availCountries.remove(countryOne);

        if (CollectionUtils.isEmpty(availCountries)) {
            return ImmutableTriple.of(countryOne, countryOne, countryOne);
        }

        Country countryTwo = generateRandomCountry(availCountries);
        availCountries.remove(countryTwo);

        if (CollectionUtils.isEmpty(availCountries)) {
            return ImmutableTriple.of(countryOne, countryTwo, countryTwo);
        }

        Country countryThree = generateRandomCountry(availCountries);

        return ImmutableTriple.of(countryOne, countryTwo, countryThree);
    }

}

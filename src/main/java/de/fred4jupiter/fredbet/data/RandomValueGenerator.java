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

    public TeamPair generateTeamPair(TeamBundle teamBundle) {
        return generateTeamPair(teamBundle.getTeams());
    }

    public TeamPair generateTeamPair(List<Country> availCountries) {
        if (CollectionUtils.isEmpty(availCountries)) {
            return null;
        }

        Country countryOne = generateRandomCountry(availCountries);
        List<Country> resultList = availCountries.stream().filter(country -> !country.equals(countryOne)).toList();

        if (CollectionUtils.isEmpty(resultList)) {
            return new TeamPair(countryOne, countryOne);
        }

        Country countryTwo = generateRandomCountry(resultList);
        return new TeamPair(countryOne, countryTwo);
    }

    public TeamTriple generateTeamTriple() {
        final List<Country> availCountries = new ArrayList<>(countryService.getAllCountriesOfMatches());
        if (CollectionUtils.isEmpty(countryService.getAllCountriesOfMatches())) {
            return null;
        }

        Country countryOne = generateRandomCountry(availCountries);
        availCountries.remove(countryOne);

        if (CollectionUtils.isEmpty(availCountries)) {
            return new TeamTriple(countryOne, countryOne, countryOne);
        }

        Country countryTwo = generateRandomCountry(availCountries);
        availCountries.remove(countryTwo);

        if (CollectionUtils.isEmpty(availCountries)) {
            return new TeamTriple(countryOne, countryTwo, countryTwo);
        }

        Country countryThree = generateRandomCountry(availCountries);

        return new TeamTriple(countryOne, countryTwo, countryThree);
    }

}

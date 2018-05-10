package de.fred4jupiter.fredbet.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.service.CountryService;

@Component
public class RandomValueGenerator {

	@Autowired
	private CountryService countryService;

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

	public ImmutablePair<Country, Country> generateTeamPair() {
		List<Country> availCountries = countryService.getAvailableCountriesWithoutNoneEntry();
		if (CollectionUtils.isEmpty(availCountries)) {
			return null;
		}

		Country countryOne = generateRandomCountry(availCountries);
		availCountries.remove(countryOne);

		if (CollectionUtils.isEmpty(availCountries)) {
			return ImmutablePair.of(countryOne, countryOne);
		}

		Country countryTwo = generateRandomCountry(availCountries);
		availCountries.remove(countryTwo);

		return ImmutablePair.of(countryOne, countryTwo);
	}

	public ImmutableTriple<Country, Country, Country> generateTeamTriple() {
		Set<Country> countries = countryService.getAvailableCountriesExtraBetsWithoutNoneEntry();
		List<Country> availCountries = new ArrayList<Country>(countries);
		if (CollectionUtils.isEmpty(availCountries)) {
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

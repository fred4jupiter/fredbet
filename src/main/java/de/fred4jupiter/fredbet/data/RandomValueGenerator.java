package de.fred4jupiter.fredbet.data;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.domain.Country;

@Component
public class RandomValueGenerator {

	private static final Logger log = LoggerFactory.getLogger(RandomValueGenerator.class);

	private final List<Country> availableCountries;

	public RandomValueGenerator() {
		this.availableCountries = Arrays.asList(Country.values()).stream()
				.filter(country -> !country.equals(Country.NONE)).collect(Collectors.toList());
	}

	public Integer generateRandomValue() {
		return generateRandomValueInRange(1, 5);
	}

	public Integer generateRandomValueInRange(int min, int max) {
		Random rn = new Random();
		int range = max - min + 1;
		return rn.nextInt(range) + min;
	}

	private Country generateRandomCountry() {
		Integer randomVal = generateRandomValueInRange(0, availableCountries.size() - 1);
		return availableCountries.get(randomVal);
	}

	private Country getOtherCountryThan(Country alreadyUsedCountry) {
		for (Country country : this.availableCountries) {
			if (!country.equals(alreadyUsedCountry)) {
				return country;
			}
		}
		throw new IllegalArgumentException("Could not found other country than given one: " + alreadyUsedCountry);
	}

	public List<Country> generateTeamPair() {
		Country countryOne = generateRandomCountry();
		Country countryTwo = generateRandomCountry();

		while (countryOne.equals(countryTwo)) {
			countryTwo = getOtherCountryThan(countryOne);
		}

		log.debug("countryOne: {}, countryTwo: {}", countryOne, countryTwo);
		return Arrays.asList(countryOne, countryTwo);
	}
}

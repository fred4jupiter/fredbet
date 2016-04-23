package de.fred4jupiter.fredbet.data;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.domain.Country;

@Component
public class RandomValueGenerator {

	public Integer generateRandomValue() {
		return generateRandomValueInRange(1, 5);
	}
	
	public Integer generateRandomValueInRange(int min, int max) {
		Random rn = new Random();
		int range = max - min + 1;
		return rn.nextInt(range) + min;
	}

	public Country generateRandomCountry() {
		List<Country> countryList = Arrays.asList(Country.values());
		Integer randomVal = generateRandomValueInRange(0, countryList.size() - 1);
		return countryList.get(randomVal);
	}
}

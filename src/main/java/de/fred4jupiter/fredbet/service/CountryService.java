package de.fred4jupiter.fredbet.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.Country;

@Service
public class CountryService {

	public List<Country> getAvailableCountries() {
		List<Country> countries = Arrays.asList(Country.values());
		Collections.sort(countries, (Country country1, Country country2) -> {
			if (Country.NONE.equals(country1)) {
				return -1;
			}
			return country1.compareTo(country2);
		});

		return countries;
	}
}

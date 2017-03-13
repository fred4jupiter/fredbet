package de.fred4jupiter.fredbet.service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.Country;

@Service
public class CountryService {

	public List<Country> getAvailableCountries() {
		LinkedList<Country> result = new LinkedList<>();
		result.addAll(getAvailableCountriesSortedWithoutNoneEntry());
		result.addFirst(Country.NONE);
		return result;
	}

	public List<Country> getAvailableCountriesSortedWithoutNoneEntry() {
		List<Country> countriesWithoutNoneEntry = getAvailableCountriesWithoutNoneEntry();
		countriesWithoutNoneEntry.sort((Country country1, Country country2) -> country1.name().compareTo(country2.name()));
		return countriesWithoutNoneEntry;
	}

	public List<Country> getAvailableCountriesWithoutNoneEntry() {
		return Arrays.asList(Country.values()).stream().filter(country -> !country.equals(Country.NONE)).collect(Collectors.toList());
	}
}

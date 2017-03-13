package de.fred4jupiter.fredbet.service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;

@Service
public class CountryService {

	@Autowired
	private MessageSourceUtil messageSourceUtil;

	public List<Country> getAvailableCountriesSortedWithNoneEntryByLocale(Locale locale) {
		LinkedList<Country> result = new LinkedList<>();
		result.addAll(getAvailableCountriesSortedWithoutNoneEntry(locale));
		result.addFirst(Country.NONE);
		return result;
	}

	public List<Country> getAvailableCountriesSortedWithoutNoneEntry(Locale locale) {
		List<Country> countriesWithoutNoneEntry = getAvailableCountriesWithoutNoneEntry();
		countriesWithoutNoneEntry.sort((Country country1, Country country2) -> {
			String countryName1 = messageSourceUtil.getCountryName(country1, locale);
			String countryName2 = messageSourceUtil.getCountryName(country2, locale);
			return countryName1.compareTo(countryName2);
		});
		return countriesWithoutNoneEntry;
	}

	public List<Country> getAvailableCountriesWithoutNoneEntry() {
		return Arrays.asList(Country.values()).stream().filter(country -> !country.equals(Country.NONE)).collect(Collectors.toList());
	}
}

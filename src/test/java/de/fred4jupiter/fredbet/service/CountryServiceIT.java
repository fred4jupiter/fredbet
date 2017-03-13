package de.fred4jupiter.fredbet.service;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fred4jupiter.fredbet.domain.Country;

@RunWith(MockitoJUnitRunner.class)
public class CountryServiceIT {

	private static final Logger LOG = LoggerFactory.getLogger(CountryServiceIT.class);

	@InjectMocks
	private CountryService countryService;

	@Test
	public void availableCountriesDoesNotContainNoneEntry() {
		List<Country> countries = countryService.getAvailableCountriesSortedWithoutNoneEntry();
		assertNotNull(countries);

		assertThat(countries, not(hasItem(Country.NONE)));
		
		LOG.debug(""+countries);
	}

	@Test
	public void getAvailableCountries() {
		List<Country> countries = countryService.getAvailableCountries();
		assertNotNull(countries);

		assertThat(countries, hasItem(Country.NONE));
	}

}

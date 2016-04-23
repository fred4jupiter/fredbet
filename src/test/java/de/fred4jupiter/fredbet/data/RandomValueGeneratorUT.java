package de.fred4jupiter.fredbet.data;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fred4jupiter.fredbet.domain.Country;

@RunWith(MockitoJUnitRunner.class)
public class RandomValueGeneratorUT {

	private static final Logger LOG = LoggerFactory.getLogger(RandomValueGeneratorUT.class);
	
	@InjectMocks
	private RandomValueGenerator randomValueGenerator;

	@Test
	public void valueFromOneToTen() {
		assertThat(randomValueGenerator.generateRandomValueInRange(1, 10), greaterThanOrEqualTo(1));
		assertThat(randomValueGenerator.generateRandomValueInRange(1, 10), lessThanOrEqualTo(10));
	}
	
	@Test
	public void generateRandomCountry() {
		List<Country> countryList = Arrays.asList(Country.values());
		Country country = randomValueGenerator.generateRandomCountry();
		assertNotNull(country);
		assertTrue(countryList.contains(country));
		LOG.debug("country={}", country);
	}
}

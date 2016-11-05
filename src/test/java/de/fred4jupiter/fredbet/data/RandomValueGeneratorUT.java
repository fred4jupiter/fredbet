package de.fred4jupiter.fredbet.data;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import de.fred4jupiter.fredbet.domain.Country;

@RunWith(MockitoJUnitRunner.class)
public class RandomValueGeneratorUT {

	@InjectMocks
	private RandomValueGenerator randomValueGenerator;

	@Test
	public void valueFromOneToTen() {
		assertThat(randomValueGenerator.generateRandomValueInRange(1, 10), greaterThanOrEqualTo(1));
		assertThat(randomValueGenerator.generateRandomValueInRange(1, 10), lessThanOrEqualTo(10));
	}

	@Test
	public void generateTeamPair() {
		for (int i = 0; i < 100; i++) {
			List<Country> countries = randomValueGenerator.generateTeamPair();
			assertEquals(2, countries.size());
			Country countryOne = countries.get(0);
			Country countryTwo = countries.get(1);
			assertNotNull(countryOne);
			assertNotNull(countryTwo);
			assertNotEquals(countryOne, countryTwo);
			assertNotEquals(Country.NONE, countryOne);
			assertNotEquals(Country.NONE, countryTwo);
		}
	}

}

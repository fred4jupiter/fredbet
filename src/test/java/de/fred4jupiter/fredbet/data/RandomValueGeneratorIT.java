package de.fred4jupiter.fredbet.data;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.fred4jupiter.fredbet.AbstractIntegrationTest;
import de.fred4jupiter.fredbet.domain.Country;

public class RandomValueGeneratorIT extends AbstractIntegrationTest {

	@Autowired
	private RandomValueGenerator randomValueGenerator;
	
	@Autowired
	private DataBasePopulator dataBasePopulator;

	@Test
	public void valueFromOneToTen() {
		assertThat(randomValueGenerator.generateRandomValueInRange(1, 10), greaterThanOrEqualTo(1));
		assertThat(randomValueGenerator.generateRandomValueInRange(1, 10), lessThanOrEqualTo(10));
	}

	@Test
	public void generateTeamPair() {
		for (int i = 0; i < 100; i++) {
			ImmutablePair<Country, Country> teeamPair = randomValueGenerator.generateTeamPair();
			Country countryOne = teeamPair.getLeft();
			Country countryTwo = teeamPair.getRight();
			assertNotNull(countryOne);
			assertNotNull(countryTwo);
			assertNotEquals(countryOne, countryTwo);
			assertNotEquals(Country.NONE, countryOne);
			assertNotEquals(Country.NONE, countryTwo);
		}
	}
	
	@Test
	public void generateTeamTriple() {
		dataBasePopulator.createRandomMatches();
		
		for (int i = 0; i < 100; i++) {
			ImmutableTriple<Country,Country,Country> triple = randomValueGenerator.generateTeamTriple();
			assertNotNull(triple);
			Country countryOne = triple.getLeft();
			Country countryTwo = triple.getMiddle();
			Country countryThree = triple.getRight();
			assertNotNull(countryOne);
			assertNotNull(countryTwo);
			assertNotNull(countryThree);
			assertNotEquals(countryOne, countryTwo);
			assertNotEquals(countryTwo, countryThree);
			assertNotEquals(countryThree, countryOne);
			assertNotEquals(Country.NONE, countryOne);
			assertNotEquals(Country.NONE, countryTwo);
			assertNotEquals(Country.NONE, countryThree);
		}
	}

}

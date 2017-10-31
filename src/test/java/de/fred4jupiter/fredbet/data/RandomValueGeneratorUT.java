package de.fred4jupiter.fredbet.data;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.service.CountryService;

@RunWith(MockitoJUnitRunner.class)
public class RandomValueGeneratorUT {

	@InjectMocks
	private RandomValueGenerator randomValueGenerator;

	@Mock
	private CountryService countryService;

	@Test
	public void generateTripleButOnlyOneCountryAvailable() {
		when(countryService.getAvailableCountriesExtraBetsWithoutNoneEntry())
				.thenReturn(new HashSet<Country>(Arrays.asList(Country.RUSSIA)));

		ImmutableTriple<Country, Country, Country> triple = randomValueGenerator.generateTeamTriple();
		assertNotNull(triple);
	}

}

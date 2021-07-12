package de.fred4jupiter.fredbet.data;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.service.CountryService;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
public class RandomValueGeneratorUT {

    @InjectMocks
    private RandomValueGenerator randomValueGenerator;

    @Mock
    private CountryService countryService;

    @Test
    public void generateTripleButOnlyOneCountryAvailable() {
        when(countryService.getAvailableCountriesWithoutNoneEntry()).thenReturn(new HashSet<>(Collections.singletonList(Country.RUSSIA)));

        ImmutableTriple<Country, Country, Country> triple = randomValueGenerator.generateTeamTriple();
        assertThat(triple).isNotNull();
    }

}

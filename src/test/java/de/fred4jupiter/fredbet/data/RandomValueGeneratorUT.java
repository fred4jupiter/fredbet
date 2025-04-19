package de.fred4jupiter.fredbet.data;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.country.CountryService;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
public class RandomValueGeneratorUT {

    private static final Logger LOG = LoggerFactory.getLogger(RandomValueGeneratorUT.class);

    @InjectMocks
    private RandomValueGenerator randomValueGenerator;

    @Mock
    private CountryService countryService;

    @Test
    public void generateTripleButOnlyOneCountryAvailable() {
        when(countryService.getAllCountriesOfMatches()).thenReturn(List.of(Country.GERMANY));

        ImmutableTriple<Country, Country, Country> triple = randomValueGenerator.generateTeamTriple();
        LOG.debug("triple: {}", triple);
        assertThat(triple).isNotNull();
        assertThat(triple.left).isNotNull();
        assertThat(triple.middle).isNotNull();
        assertThat(triple.right).isNotNull();
    }

    @Test
    public void threeDistinct() {
        when(countryService.getAllCountriesOfMatches()).thenReturn(List.of(Country.GERMANY, Country.FRANCE, Country.CHILE));

        ImmutableTriple<Country, Country, Country> triple = randomValueGenerator.generateTeamTriple();
        LOG.debug("triple: {}", triple);
        assertThat(triple).isNotNull();
        assertThat(triple.left).isNotNull();
        assertThat(triple.middle).isNotNull();
        assertThat(triple.right).isNotNull();
    }

}

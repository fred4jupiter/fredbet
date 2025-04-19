package de.fred4jupiter.fredbet.data;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.country.CountryService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        TeamTriple triple = randomValueGenerator.generateTeamTriple();
        LOG.debug("triple: {}", triple);
        assertThat(triple).isNotNull();
        assertThat(triple.finalWinner()).isNotNull();
        assertThat(triple.semiFinalWinner()).isNotNull();
        assertThat(triple.thirdFinalWinner()).isNotNull();
    }

    @Test
    public void threeDistinct() {
        when(countryService.getAllCountriesOfMatches()).thenReturn(List.of(Country.GERMANY, Country.FRANCE, Country.CHILE));

        TeamTriple triple = randomValueGenerator.generateTeamTriple();
        LOG.debug("triple: {}", triple);
        assertThat(triple).isNotNull();
        assertThat(triple.finalWinner()).isNotNull();
        assertThat(triple.semiFinalWinner()).isNotNull();
        assertThat(triple.thirdFinalWinner()).isNotNull();
    }

}

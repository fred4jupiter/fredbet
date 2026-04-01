package de.fred4jupiter.fredbet.data;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.match.MatchRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static de.fred4jupiter.fredbet.domain.Country.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
public class RandomValueGeneratorUT {

    private static final Logger LOG = LoggerFactory.getLogger(RandomValueGeneratorUT.class);

    @InjectMocks
    private RandomValueGenerator randomValueGenerator;

    @Mock
    private MatchRepository matchRepository;

    @Test
    public void generateTripleButOnlyOneCountryAvailable() {
        when(matchRepository.getAllCountriesOfMatches()).thenReturn(List.of(Country.GERMANY));

        TeamTriple triple = randomValueGenerator.generateTeamTriple();
        LOG.debug("triple: {}", triple);
        assertThat(triple).isNotNull();
        assertThat(triple.finalWinner()).isNotNull();
        assertThat(triple.semiFinalWinner()).isNotNull();
        assertThat(triple.thirdFinalWinner()).isNotNull();
    }

    @Test
    public void threeDistinct() {
        when(matchRepository.getAllCountriesOfMatches()).thenReturn(List.of(Country.GERMANY, Country.FRANCE, Country.CHILE));

        TeamTriple triple = randomValueGenerator.generateTeamTriple();
        LOG.debug("triple: {}", triple);
        assertThat(triple).isNotNull();
        assertThat(triple.finalWinner()).isNotNull();
        assertThat(triple.semiFinalWinner()).isNotNull();
        assertThat(triple.thirdFinalWinner()).isNotNull();
    }

    @Test
    public void distinctRandomElements() {
        final List<Country> list = List.of(AL_AHLY, ATLETICO_MADRID, BOTAFOGO, FC_PORTO, INTER_MIAMI, MANCHESTER_CITY);

        List<Country> countries = randomValueGenerator.distinctRandomElements(list, 3);
        LOG.debug("countries: {}", countries);
        assertThat(countries).hasSize(3);

        Country one = countries.get(0);
        Country two = countries.get(1);
        Country three = countries.get(2);

        assertThat(one).isNotEqualTo(two);
        assertThat(one).isNotEqualTo(three);
        assertThat(two).isNotEqualTo(three);
    }

}

package de.fred4jupiter.fredbet.data;

import de.fred4jupiter.fredbet.IntegrationTest;
import de.fred4jupiter.fredbet.domain.Country;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public class RandomValueGeneratorIT {

    @Autowired
    private RandomValueGenerator randomValueGenerator;

    @Autowired
    private DatabasePopulator dataBasePopulator;

    @Test
    public void valueFromOneToTen() {
        assertThat(randomValueGenerator.generateRandomValueInRange(1, 10)).isGreaterThanOrEqualTo(1);
        assertThat(randomValueGenerator.generateRandomValueInRange(1, 10)).isLessThanOrEqualTo(10);
    }

    @Test
    public void generateTeamPair() {
        for (int i = 0; i < 100; i++) {
            ImmutablePair<Country, Country> teeamPair = randomValueGenerator.generateTeamPair();
            Country countryOne = teeamPair.getLeft();
            Country countryTwo = teeamPair.getRight();
            assertThat(countryOne).isNotNull();
            assertThat(countryTwo).isNotNull();
            assertThat(countryOne).isNotEqualTo(countryTwo);

            assertThat(Country.NONE).isNotEqualTo(countryOne);
            assertThat(Country.NONE).isNotEqualTo(countryTwo);
        }
    }

    @Test
    public void generateTeamTriple() {
        dataBasePopulator.createRandomMatches();

        for (int i = 0; i < 100; i++) {
            ImmutableTriple<Country, Country, Country> triple = randomValueGenerator.generateTeamTriple();
            assertThat(triple).isNotNull();
            Country countryOne = triple.getLeft();
            Country countryTwo = triple.getMiddle();
            Country countryThree = triple.getRight();
            assertThat(countryOne).isNotNull();
            assertThat(countryTwo).isNotNull();
            assertThat(countryThree).isNotNull();

            assertThat(countryOne).isNotEqualTo(countryTwo);
            assertThat(countryTwo).isNotEqualTo(countryThree);
            assertThat(countryThree).isNotEqualTo(countryOne);

            assertThat(Country.NONE).isNotEqualTo(countryOne);
            assertThat(Country.NONE).isNotEqualTo(countryTwo);
            assertThat(Country.NONE).isNotEqualTo(countryThree);
        }
    }

}

package de.fred4jupiter.fredbet.data;

import de.fred4jupiter.fredbet.common.TransactionalIntegrationTest;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.match.MatchRepository;
import de.fred4jupiter.fredbet.teambundle.TeamBundle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TransactionalIntegrationTest
public class RandomValueGeneratorIT {

    @Autowired
    private RandomValueGenerator randomValueGenerator;

    @Autowired
    private DatabasePopulator dataBasePopulator;

    @Autowired
    private MatchRepository matchRepository;

    @Test
    public void valueFromOneToTen() {
        GoalResult goalResult = randomValueGenerator.generateGoalResult(1, 10);
        assertThat(goalResult).isNotNull();
        assertThat(goalResult.goalsTeamOne()).isGreaterThanOrEqualTo(1);
        assertThat(goalResult.goalsTeamOne()).isLessThanOrEqualTo(10);
        assertThat(goalResult.goalsTeamTwo()).isGreaterThanOrEqualTo(1);
        assertThat(goalResult.goalsTeamTwo()).isLessThanOrEqualTo(10);
    }

    @Test
    public void generateTeamPair() {
        for (int i = 0; i < 100; i++) {
            TeamPair teeamPair = randomValueGenerator.generateTeamPair(TeamBundle.WORLD_CUP);
            Country countryOne = teeamPair.teamOne();
            Country countryTwo = teeamPair.teamTwo();
            assertThat(countryOne).isNotNull();
            assertThat(countryTwo).isNotNull();
            assertThat(countryOne).isNotEqualTo(countryTwo);
        }
    }

    @Test
    public void generateTeamTriple() {
        dataBasePopulator.createDemoData(new DemoDataCreation(TeamBundle.WORLD_CUP, 6, false, false));

        List<Country> allCountriesOfMatches = matchRepository.getAllCountriesOfMatches();

        for (int i = 0; i < 100; i++) {
            TeamTriple triple = randomValueGenerator.generateTeamTriple();
            assertThat(triple).isNotNull();
            Country countryOne = triple.finalWinner();
            Country countryTwo = triple.semiFinalWinner();
            Country countryThree = triple.thirdFinalWinner();
            assertThat(countryOne).isNotNull();
            assertThat(countryTwo).isNotNull();
            assertThat(countryThree).isNotNull();

            if (allCountriesOfMatches.size() > 3) {
                assertThat(countryOne).isNotEqualTo(countryTwo);
                assertThat(countryTwo).isNotEqualTo(countryThree);
                assertThat(countryThree).isNotEqualTo(countryOne);
            }
        }
    }

}

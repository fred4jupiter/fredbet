package de.fred4jupiter.fredbet.data;

import de.fred4jupiter.fredbet.team.TeamService;
import de.fred4jupiter.fredbet.common.TransactionalIntegrationTest;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.match.MatchRepository;
import de.fred4jupiter.fredbet.teambundle.TeamBundle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TransactionalIntegrationTest
public class RandomValueGeneratorIT {

    @Autowired
    private RandomValueGenerator randomValueGenerator;

    @Autowired
    private DataPopulator dataBasePopulator;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private TeamService teamService;

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
        List<Team> teams = List.of(new Team(Country.GERMANY), new Team(Country.FRANCE), new Team(Country.SPAIN));
        for (int i = 0; i < 100; i++) {
            TeamPair teamPair = randomValueGenerator.generateTeamPair(teams);
            assertThat(teamPair.teamOne()).isNotNull();
            assertThat(teamPair.teamTwo()).isNotNull();
            assertThat(teamPair.teamOne()).isNotEqualTo(teamPair.teamTwo());
        }
    }

    @Test
    public void generateTeamPair_withLessThanTwoTeams_throwsException() {
        assertThatThrownBy(() -> randomValueGenerator.generateTeamPair(List.of(new Team(Country.GERMANY))))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void generateTeamTriple() {
        teamService.createTeamsOfBundle(TeamBundle.EUROPEAN_CHAMPIONSHIP);
        dataBasePopulator.createDemoData(new DemoDataCreation(GroupSelection.ROUND_OF_SIXTEEN, false, false, false));

        List<Team> allTeamsOfMatches = matchRepository.getAllTeamsOfMatches();

        for (int i = 0; i < 100; i++) {
            TeamTriple triple = randomValueGenerator.generateTeamTriple();
            assertThat(triple).isNotNull();
            Team countryOne = triple.finalWinner();
            Team countryTwo = triple.semiFinalWinner();
            Team countryThree = triple.thirdFinalWinner();
            assertThat(countryOne).isNotNull();
            assertThat(countryTwo).isNotNull();
            assertThat(countryThree).isNotNull();

            if (allTeamsOfMatches.size() > 3) {
                assertThat(countryOne).isNotEqualTo(countryTwo);
                assertThat(countryTwo).isNotEqualTo(countryThree);
                assertThat(countryThree).isNotEqualTo(countryOne);
            }
        }
    }

}
